package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.innerfriends.userprofilepicture.domain.FeatureState;
import com.innerfriends.userprofilepicture.domain.UserProfilePicture;
import com.innerfriends.userprofilepicture.domain.UserProfilePictures;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.infrastructure.opentelemetry.NewSpan;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.CachedUserProfilePictures;
import com.innerfriends.userprofilepicture.infrastructure.usecase.cache.UserProfilePicturesCacheRepository;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class HazelcastUserProfilePicturesCacheRepository implements UserProfilePicturesCacheRepository {

    private static final Logger LOG = Logger.getLogger(HazelcastUserProfilePicturesCacheRepository.class);

    public static final String MAP_NAME = "userProfilePicture";

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final HazelcastInstance hazelcastInstance;

    public HazelcastUserProfilePicturesCacheRepository(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @NewSpan
    @Override
    public Optional<CachedUserProfilePictures> get(final UserPseudo userPseudo) {
        return Optional.ofNullable(hazelcastInstance.getMap(MAP_NAME).get(userPseudo.pseudo()))
                .map(String.class::cast)
                .map(value -> readFromJson(value));
    }

    @NewSpan
    @Override
    public void store(final UserPseudo userPseudo, final UserProfilePictures userProfilePictures) {
        if (userProfilePictures.canBeStoredInCache()) {
            final HazelcastCachedUserProfilePictures hazelcastCachedUserProfilePictures = Optional.ofNullable(hazelcastInstance.getMap(MAP_NAME).get(userPseudo.pseudo()))
                    .map(String.class::cast)
                    .map(inCache -> readFromJson(inCache))
                    .map(inCache ->
                            inCache.replaceUserProfilePictures(
                                    userProfilePictures.userProfilePictures()
                                            .stream()
                                            .map(userProfilePicture -> HazelcastUserProfilePicture
                                                    .newBuilder()
                                                    .withUserPseudo(userProfilePicture.userPseudo().pseudo())
                                                    .withMediaType(userProfilePicture.mediaType())
                                                    .withVersionId(userProfilePicture.versionId().version())
                                                    .withFeatured(userProfilePicture.isFeatured())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .replaceFeaturedUserProfilePictureIdentifier(
                                            userProfilePictures.userProfilePictures()
                                                    .stream()
                                                    .filter(UserProfilePicture::isFeatured)
                                                    .map(HazelcastUserProfilePictureIdentifier::new)
                                                    .findFirst()
                                                    .orElseThrow(() -> new IllegalStateException("Must have one featured")))
                                    .replaceFeatureState(userProfilePictures.featureState()))
                    .orElseGet(() -> HazelcastCachedUserProfilePictures.newBuilder()
                            .withUserPseudo(userPseudo.pseudo())
                            .withUserProfilePictures(
                                    userProfilePictures.userProfilePictures()
                                            .stream()
                                            .map(userProfilePicture -> HazelcastUserProfilePicture
                                                    .newBuilder()
                                                    .withUserPseudo(userProfilePicture.userPseudo().pseudo())
                                                    .withMediaType(userProfilePicture.mediaType())
                                                    .withVersionId(userProfilePicture.versionId().version())
                                                    .withFeatured(userProfilePicture.isFeatured())
                                                    .build())
                                            .collect(Collectors.toList()))
                            .withFeaturedUserProfilePictureIdentifier(
                                    userProfilePictures.userProfilePictures()
                                            .stream()
                                            .filter(UserProfilePicture::isFeatured)
                                            .map(HazelcastUserProfilePictureIdentifier::new)
                                            .findFirst()
                                            .orElseThrow(() -> new IllegalStateException("Must have one featured")))
                            .withFeatureState(userProfilePictures.featureState())
                            .build());
            hazelcastInstance.getMap(MAP_NAME).put(userPseudo.pseudo(),
                    writeFrom(hazelcastCachedUserProfilePictures));
        }
    }

    @NewSpan
    @Override
    public void storeFeatured(final UserProfilePicture userProfilePicture) {
        final HazelcastCachedUserProfilePictures hazelcastCachedUserProfilePictures = Optional.ofNullable(hazelcastInstance.getMap(MAP_NAME).get(userProfilePicture.userPseudo().pseudo()))
                .map(String.class::cast)
                .map(inCache -> readFromJson(inCache))
                .map(inCache ->
                        inCache.replaceUserProfilePictures(null)
                                .replaceFeaturedUserProfilePictureIdentifier(new HazelcastUserProfilePictureIdentifier(userProfilePicture))
                                .replaceFeatureState(FeatureState.SELECTED))
                .orElseGet(() -> HazelcastCachedUserProfilePictures.newBuilder()
                        .withUserPseudo(userProfilePicture.userPseudo().pseudo())
                        .withFeaturedUserProfilePictureIdentifier(new HazelcastUserProfilePictureIdentifier(userProfilePicture))
                        .withFeatureState(FeatureState.SELECTED)
                        .build());
        hazelcastInstance.getMap(MAP_NAME).put(userProfilePicture.userPseudo().pseudo(),
                writeFrom(hazelcastCachedUserProfilePictures));
    }

    @NewSpan
    @Override
    public void evict(final UserPseudo userPseudo) {
        hazelcastInstance.getMap(MAP_NAME).remove(userPseudo.pseudo());
    }

    private HazelcastCachedUserProfilePictures readFromJson(final String json) {
        try {
            return objectMapper.readValue(json, HazelcastCachedUserProfilePictures.class);
        } catch (final JsonProcessingException e) {
            LOG.error("Unable to read from json", e);
            throw new RuntimeException(e);
        }
    }

    private String writeFrom(final HazelcastCachedUserProfilePictures hazelcastCachedUserProfilePictures) {
        try {
            return objectMapper.writeValueAsString(hazelcastCachedUserProfilePictures);
        } catch (JsonProcessingException e) {
            LOG.error("Unable to write to HazelcastCachedUserProfilePictures", e);
            throw new RuntimeException(e);
        }
    }

}

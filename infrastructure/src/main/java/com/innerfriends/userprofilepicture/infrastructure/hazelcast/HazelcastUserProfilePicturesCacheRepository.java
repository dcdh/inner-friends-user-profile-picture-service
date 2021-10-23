package com.innerfriends.userprofilepicture.infrastructure.hazelcast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
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
                    .map(value -> readFromJson(value))
                    .orElseGet(() -> HazelcastCachedUserProfilePictures.newBuilder().setUserPseudo(userPseudo.pseudo()).build())
                    .replaceAllProfilePictureIdentifiers(
                            userProfilePictures.userProfilePictures().stream()
                                    .map(HazelcastUserProfilePictureIdentifier::new)
                                    .collect(Collectors.toList()));
            hazelcastInstance.getMap(MAP_NAME).put(userPseudo.pseudo(),
                    writeFrom(hazelcastCachedUserProfilePictures));
        }
    }

    @NewSpan
    @Override
    public void storeFeatured(final UserPseudo userPseudo, final UserProfilePictureIdentifier featured) {
        final HazelcastCachedUserProfilePictures hazelcastCachedUserProfilePictures = Optional.ofNullable(hazelcastInstance.getMap(MAP_NAME).get(userPseudo.pseudo()))
                .map(String.class::cast)
                .map(value -> readFromJson(value))
                .orElseGet(() -> HazelcastCachedUserProfilePictures.newBuilder().setUserPseudo(userPseudo.pseudo()).build())
                .setFeaturedUserProfilePictureIdentifier(new HazelcastUserProfilePictureIdentifier(featured));
        hazelcastInstance.getMap(MAP_NAME).put(userPseudo.pseudo(),
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

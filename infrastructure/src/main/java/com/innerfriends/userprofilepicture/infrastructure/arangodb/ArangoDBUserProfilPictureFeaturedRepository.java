package com.innerfriends.userprofilepicture.infrastructure.arangodb;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.OverwriteMode;
import com.innerfriends.userprofilepicture.domain.UserProfilPictureFeaturedRepository;
import com.innerfriends.userprofilepicture.domain.UserProfilPictureFeaturedRepositoryException;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class ArangoDBUserProfilPictureFeaturedRepository implements UserProfilPictureFeaturedRepository {

    public static final String COLLECTION_FEATURE = "FEATURE";

    private final ArangoDB arangoDB;
    private final String dbName;
    private final ManagedExecutor managedExecutor;
    private ArangoDatabase arangoDatabase;

    private static final Logger LOG = Logger.getLogger(ArangoDBUserProfilPictureFeaturedRepository.class);

    public ArangoDBUserProfilPictureFeaturedRepository(final ArangoDB arangoDB,
                                                       @ConfigProperty(name = "arangodb.dbName") final String dbName,
                                                       final ManagedExecutor managedExecutor) {
        this.arangoDB = Objects.requireNonNull(arangoDB);
        this.dbName = Objects.requireNonNull(dbName);
        this.managedExecutor = Objects.requireNonNull(managedExecutor);
    }

    // TODO span interceptor
    @Override
    public Optional<UserProfilePictureIdentifier> getFeatured(final UserPseudo userPseudo) throws UserProfilPictureFeaturedRepositoryException {
        try {
            return Optional.ofNullable(arangoDatabase.collection(COLLECTION_FEATURE).getDocument(userPseudo.pseudo(), ArangoDBProfilePictureIdentifier.class));
        } catch (final ArangoDBException exception) {
            LOG.error(exception);
            throw new UserProfilPictureFeaturedRepositoryException();
        }
    }

    // TODO span interceptor
    @Override
    public UserProfilePictureIdentifier markAsFeatured(final UserProfilePictureIdentifier userProfilePictureIdentifier) throws UserProfilPictureFeaturedRepositoryException {
        try {
            arangoDatabase.collection(COLLECTION_FEATURE).insertDocument(
                    new ArangoDBProfilePictureIdentifier(userProfilePictureIdentifier),
                    new DocumentCreateOptions()
                            .waitForSync(true)
                            .overwriteMode(OverwriteMode.update));
            return userProfilePictureIdentifier;
        } catch (final ArangoDBException exception) {
            LOG.error(exception);
            throw new UserProfilPictureFeaturedRepositoryException();
        }
    }

    public void onStart(@Observes final StartupEvent ev) {
        if (!arangoDB.db(dbName).exists()) {
            arangoDB.createDatabase(dbName);
        }
        final ArangoDatabase arangoDatabase = arangoDB.db(dbName);
        if (!arangoDatabase.collection(ArangoDBUserProfilPictureFeaturedRepository.COLLECTION_FEATURE).exists()) {
            arangoDatabase.createCollection(ArangoDBUserProfilPictureFeaturedRepository.COLLECTION_FEATURE);
        }
        this.arangoDatabase = arangoDB.db(dbName);
    }

}

package com.innerfriends.userprofilepicture.infrastructure.arangodb;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.OverwriteMode;
import com.innerfriends.userprofilepicture.domain.UserProfilPictureFeaturedRepository;
import com.innerfriends.userprofilepicture.domain.UserProfilPictureFeaturedRepositoryException;
import com.innerfriends.userprofilepicture.domain.UserProfilePictureIdentifier;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import com.innerfriends.userprofilepicture.infrastructure.opentelemetry.NewSpan;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Objects;
import java.util.Optional;

//===========================================================================
// /!\ do not use Velocypack it does not seems to work in native mode !
//===========================================================================
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

    @NewSpan
    @Override
    public Optional<UserProfilePictureIdentifier> getFeatured(final UserPseudo userPseudo) throws UserProfilPictureFeaturedRepositoryException {
        try {
            return Optional.ofNullable(arangoDatabase.collection(COLLECTION_FEATURE).getDocument(userPseudo.pseudo(), BaseDocument.class))
                    .map(ArangoDBProfilePictureIdentifier::new);
        } catch (final ArangoDBException exception) {
            LOG.error(exception);
            throw new UserProfilPictureFeaturedRepositoryException();
        }
    }

    @NewSpan
    @Override
    public UserProfilePictureIdentifier markAsFeatured(final UserProfilePictureIdentifier userProfilePictureIdentifier) throws UserProfilPictureFeaturedRepositoryException {
        try {
            final BaseDocument featured = new BaseDocument();
            featured.setKey(userProfilePictureIdentifier.userPseudo().pseudo());
            featured.addAttribute("mediaType", userProfilePictureIdentifier.mediaType().name());
            featured.addAttribute("versionId", userProfilePictureIdentifier.versionId().version());
            arangoDatabase.collection(COLLECTION_FEATURE).insertDocument(
                    featured,
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

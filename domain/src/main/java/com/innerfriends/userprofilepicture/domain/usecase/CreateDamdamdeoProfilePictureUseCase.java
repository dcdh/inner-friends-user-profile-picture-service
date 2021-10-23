package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class CreateDamdamdeoProfilePictureUseCase implements UseCase<Void, CreateDamdamdeoProfilePictureCommand> {

    private final UserProfilePictureRepository userProfilePictureRepository;

    public CreateDamdamdeoProfilePictureUseCase(final UserProfilePictureRepository userProfilePictureRepository) {
        this.userProfilePictureRepository = Objects.requireNonNull(userProfilePictureRepository);
    }

    @Override
    public Void execute(final CreateDamdamdeoProfilePictureCommand command) {
        if (userProfilePictureRepository.listByUserPseudoAndMediaType(command.userPseudo(), SupportedMediaType.IMAGE_JPEG).isEmpty()) {
            final byte[] damdamdeoPicture = getAsset("Damdamdeo.jpeg");
            userProfilePictureRepository.storeNewUserProfilePicture(new DamdamdeoNewUserProfilePicture(command, damdamdeoPicture));
        }
        return null;
    }

    private byte[] getAsset(final String assetName) {
        try {
            return this.getClass().getClassLoader().getResourceAsStream("assets/" + assetName).readAllBytes();
        } catch (final IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException(ioException);
        }
    }

    public static final class DamdamdeoNewUserProfilePicture implements NewUserProfilePicture {

        private final CreateDamdamdeoProfilePictureCommand command;
        private final byte[] damdamdeoPicture;

        public DamdamdeoNewUserProfilePicture(final CreateDamdamdeoProfilePictureCommand command,
                                              final byte[] damdamdeoPicture) {
            this.command = command;
            this.damdamdeoPicture = damdamdeoPicture;
        }

        @Override
        public UserPseudo userPseudo() {
            return command.userPseudo();
        }

        @Override
        public byte[] picture() {
            return damdamdeoPicture;
        }

        @Override
        public SupportedMediaType mediaType() {
            return SupportedMediaType.IMAGE_JPEG;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DamdamdeoNewUserProfilePicture)) return false;
            DamdamdeoNewUserProfilePicture that = (DamdamdeoNewUserProfilePicture) o;
            return Objects.equals(command, that.command) &&
                    Arrays.equals(damdamdeoPicture, that.damdamdeoPicture);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(command);
            result = 31 * result + Arrays.hashCode(damdamdeoPicture);
            return result;
        }
    }

}

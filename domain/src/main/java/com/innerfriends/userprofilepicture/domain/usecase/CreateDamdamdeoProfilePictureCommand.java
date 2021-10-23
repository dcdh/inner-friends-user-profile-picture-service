package com.innerfriends.userprofilepicture.domain.usecase;

import com.innerfriends.userprofilepicture.domain.UseCaseCommand;
import com.innerfriends.userprofilepicture.domain.UserPseudo;

import java.util.Objects;

public final class CreateDamdamdeoProfilePictureCommand implements UseCaseCommand {

    @Override
    public UserPseudo userPseudo() {
        return new DamdamdeoUserPseudo();
    }

    public static final class DamdamdeoUserPseudo implements UserPseudo {
        private final String pseudo = "Damdamdeo";

        @Override
        public String pseudo() {
            return pseudo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DamdamdeoUserPseudo)) return false;
            DamdamdeoUserPseudo that = (DamdamdeoUserPseudo) o;
            return Objects.equals(pseudo, that.pseudo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pseudo);
        }
    }
}

package com.innerfriends.userprofilepicture.infrastructure.s3;

import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class S3ObjectKeyTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(S3ObjectKey.class).verify();
    }

    @Test
    public void should_fail_fast_when_user_pseudo_is_null() {
        assertThatThrownBy(() -> new S3ObjectKey(null, mock(SupportedMediaType.class)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_fail_fast_when_supported_media_type_is_null() {
        assertThatThrownBy(() -> new S3ObjectKey(mock(UserPseudo.class), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_return_object_key() {
        // Given
        final UserPseudo givenUserPseudo = mock(UserPseudo.class);
        doReturn("pseudo").when(givenUserPseudo).pseudo();
        final SupportedMediaType givenSupportedMediaType = mock(SupportedMediaType.class);
        doReturn(".jpeg").when(givenSupportedMediaType).extension();

        // When && Then
        assertThat(new S3ObjectKey(givenUserPseudo, givenSupportedMediaType).key())
                .isEqualTo("pseudo.jpeg");
    }

}

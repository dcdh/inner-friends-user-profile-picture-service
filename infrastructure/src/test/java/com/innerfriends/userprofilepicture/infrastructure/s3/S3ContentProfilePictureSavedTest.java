package com.innerfriends.userprofilepicture.infrastructure.s3;

import com.innerfriends.userprofilepicture.domain.SupportedMediaType;
import com.innerfriends.userprofilepicture.domain.UserPseudo;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class S3ContentProfilePictureSavedTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(S3UserProfilePictureSaved.class).verify();
    }

    @Test
    public void should_fail_fast_when_user_pseudo_is_null() {
        assertThatThrownBy(() -> new S3UserProfilePictureSaved(null, mock(SupportedMediaType.class), mock(PutObjectResponse.class)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_fail_fast_when_media_type_is_null() {
        assertThatThrownBy(() -> new S3UserProfilePictureSaved(mock(UserPseudo.class), null, mock(PutObjectResponse.class)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_fail_fast_when_put_object_response_is_null() {
        assertThatThrownBy(() -> new S3UserProfilePictureSaved(mock(UserPseudo.class), mock(SupportedMediaType.class), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void should_return_user_pseudo() {
        // Given
        final UserPseudo givenUserPseudo = mock(UserPseudo.class);
        final PutObjectResponse givenPutObjectResponse = mock(PutObjectResponse.class);
        doReturn("versionId").when(givenPutObjectResponse).versionId();

        // When && Then
        assertThat(new S3UserProfilePictureSaved(givenUserPseudo, mock(SupportedMediaType.class), givenPutObjectResponse).userPseudo())
                .isEqualTo(givenUserPseudo);
    }

    @Test
    public void should_return_media_type() {
        // Given
        final SupportedMediaType givenMediaType = mock(SupportedMediaType.class);
        final PutObjectResponse givenPutObjectResponse = mock(PutObjectResponse.class);
        doReturn("versionId").when(givenPutObjectResponse).versionId();

        // When && Then
        assertThat(new S3UserProfilePictureSaved(mock(UserPseudo.class), givenMediaType, givenPutObjectResponse).mediaType())
                .isEqualTo(givenMediaType);
    }

    @Test
    public void should_return_versionId() {
        // Given
        final PutObjectResponse givenPutObjectResponse = mock(PutObjectResponse.class);
        doReturn("versionId").when(givenPutObjectResponse).versionId();

        // When && Then
        assertThat(new S3UserProfilePictureSaved(mock(UserPseudo.class), mock(SupportedMediaType.class), givenPutObjectResponse).versionId())
                .isEqualTo(new S3VersionId("versionId"));
    }

}

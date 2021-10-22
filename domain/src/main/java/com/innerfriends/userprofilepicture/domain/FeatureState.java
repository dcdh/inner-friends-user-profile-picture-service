package com.innerfriends.userprofilepicture.domain;

public enum FeatureState {
    NO_PICTURE_TO_SELECT() {

        @Override
        public boolean canBeStoredInCache() {
            return true;
        }

    },
    NOT_SELECTED_YET_GET_FIRST_ONE() {

        @Override
        public boolean canBeStoredInCache() {
            return true;
        }

    },
    SELECTED() {

        @Override
        public boolean canBeStoredInCache() {
            return true;
        }

    },
    IN_ERROR_WHEN_RETRIEVING() {

        @Override
        public boolean canBeStoredInCache() {
            return false;
        }

    };

    public abstract boolean canBeStoredInCache();

}

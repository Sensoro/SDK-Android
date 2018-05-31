package com.sensoro.experience.tool;

/**
 * Created by ddong1031 on 18/4/16.
 */
public interface PermissionsResultObserve {
    void onPermissionGranted();

    void onPermissionDenied();
}

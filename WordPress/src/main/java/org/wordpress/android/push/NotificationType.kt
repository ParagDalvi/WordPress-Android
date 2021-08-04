package org.wordpress.android.push

enum class NotificationType {
    COMMENT,
    LIKE,
    COMMENT_LIKE,
    AUTOMATTCHER,
    FOLLOW,
    REBLOG,
    BADGE_RESET,
    NOTE_DELETE,
    TEST_NOTE,
    ZENDESK,
    UNKNOWN_NOTE,
    AUTHENTICATION,
    GROUP_NOTIFICATION,
    ACTIONS_RESULT,
    ACTIONS_PROGRESS,
    PENDING_DRAFTS,
    QUICK_START_REMINDER,
    POST_UPLOAD_SUCCESS,
    POST_UPLOAD_ERROR,
    MEDIA_UPLOAD_SUCCESS,
    MEDIA_UPLOAD_ERROR,
    POST_PUBLISHED,
    STORY_SAVE_SUCCESS,
    STORY_SAVE_ERROR,
    STORY_FRAME_SAVE_SUCCESS,
    STORY_FRAME_SAVE_ERROR,
    BLOGGING_REMINDERS;
}

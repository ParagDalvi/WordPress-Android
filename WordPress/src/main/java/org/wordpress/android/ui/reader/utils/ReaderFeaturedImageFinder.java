package org.wordpress.android.ui.reader.utils;

import android.net.Uri;

import org.wordpress.android.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * used when a post doesn't have a featured image assigned, searches post's content
 * for an image that may be large enough to be suitable as a featured image
 * USAGE: new ReaderFeaturedImageFinder(content).getBestFeaturedImage()
 */
public class ReaderFeaturedImageFinder {
    private final String mContent;
    private static final int MIN_FEATURED_IMAGE_WIDTH = 500;

    // regex for matching img tags in html content
    private static final Pattern IMG_TAG_PATTERN = Pattern.compile(
            "<img(\\s+.*?)(?:src\\s*=\\s*(?:'|\")(.*?)(?:'|\"))(.*?)/>",
            Pattern.DOTALL| Pattern.CASE_INSENSITIVE);

    // regex for matching width attributes in tags
    private static final Pattern WIDTH_ATTR_PATTERN = Pattern.compile(
            "width\\s*=\\s*(?:'|\")(.*?)(?:'|\")",
            Pattern.DOTALL|Pattern.CASE_INSENSITIVE);

    // regex for matching src attributes in tags
    private static final Pattern SRC_ATTR_PATTERN = Pattern.compile(
            "src\\s*=\\s*(?:'|\")(.*?)(?:'|\")",
            Pattern.DOTALL|Pattern.CASE_INSENSITIVE);

    public ReaderFeaturedImageFinder(final String contentOfPost) {
        mContent = contentOfPost;
    }

    /*
     * returns the url of the largest image based on the w= query param and/or the width
     * attribute, provided that the width is at least MIN_FEATURED_IMAGE_WIDTH
     */
    public String getBestFeaturedImage() {
        if (mContent == null || !mContent.contains("<img ")) {
            return null;
        }

        String currentImageUrl = null;
        int currentMaxWidth = MIN_FEATURED_IMAGE_WIDTH;

        Matcher imgMatcher = IMG_TAG_PATTERN.matcher(mContent);
        while (imgMatcher.find()) {
            String imgTag = mContent.substring(imgMatcher.start(), imgMatcher.end());
            String imageUrl = getSrcAttrValue(imgTag);

            int width = Math.max(getWidthAttrValue(imgTag), getIntQueryParam(imageUrl, "w"));
            if (width > currentMaxWidth) {
                currentImageUrl = imageUrl;
                currentMaxWidth = width;
            }
        }

        return currentImageUrl;
    }

    /*
     * returns the integer value from the width attribute in the passed html tag
     */
    private int getWidthAttrValue(final String tag) {
        if (tag == null) {
            return 0;
        }

        Matcher matcher = WIDTH_ATTR_PATTERN.matcher(tag);
        if (matcher.find()) {
            // remove "width=" and quotes from the result
            return StringUtils.stringToInt(tag.substring(matcher.start() + 7, matcher.end() - 1), 0);
        } else {
            return 0;
        }
    }

    /*
     * returns the value from the src attribute in the passed html tag
     */
    private String getSrcAttrValue(final String tag) {
        if (tag == null) {
            return null;
        }

        Matcher matcher = SRC_ATTR_PATTERN.matcher(tag);
        if (matcher.find()) {
            // remove "src=" and quotes from the result
            return tag.substring(matcher.start() + 5, matcher.end() - 1);
        } else {
            return null;
        }
    }

    /*
     * returns the integer value of the passed query param in the passed url - returns zero
     * if the url is invalid, or the param doesn't exist, or the param value could not be
     * converted to an int
     */
    private int getIntQueryParam(final String url, final String param) {
        if (url == null
                || param == null
                || !url.startsWith("http")
                || !url.contains(param + "=")) {
            return 0;
        }
        return StringUtils.stringToInt(Uri.parse(url).getQueryParameter(param));
    }
}

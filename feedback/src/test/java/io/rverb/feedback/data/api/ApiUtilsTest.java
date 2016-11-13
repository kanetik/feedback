package io.rverb.feedback.data.api;

import junit.framework.Assert;

import org.junit.Test;

import okhttp3.OkHttpClient;

public class ApiUtilsTest {
    @Test
    public void canGetOkHttpClient() throws Exception {
        Assert.assertTrue(ApiUtils.getOkHttpClient() instanceof OkHttpClient);
    }
}

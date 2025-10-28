package com.example.cashi.bdd.cucumber

import io.cucumber.junit.platform.engine.Constants
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.ConfigurationParameter

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = Constants.GLUE_PROPERTY_NAME,
    value = "com.example.cashi.bdd.cucumber.steps"
)
@ConfigurationParameter(
    key = Constants.PLUGIN_PROPERTY_NAME,
    value = "pretty, summary"
)
@ConfigurationParameter(
    key = Constants.FILTER_TAGS_PROPERTY_NAME,
    value = "not @ignore"
)
class CucumberLauncher
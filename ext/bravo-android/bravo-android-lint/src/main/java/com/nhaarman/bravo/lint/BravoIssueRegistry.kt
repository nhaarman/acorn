package com.nhaarman.bravo.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class BravoIssueRegistry : IssueRegistry() {

    override val api = CURRENT_API

    override val issues: List<Issue>
        get() = listOf(RestorableViewUsageDetector.issue)
}
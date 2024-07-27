package org.stt.reporting

import org.stt.model.Report

interface ReportGenerator {
    fun createReport(): Report
}
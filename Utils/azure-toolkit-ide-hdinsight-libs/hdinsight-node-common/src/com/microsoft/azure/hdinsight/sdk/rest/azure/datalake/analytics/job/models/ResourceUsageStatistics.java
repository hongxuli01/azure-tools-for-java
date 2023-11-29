/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.hdinsight.sdk.rest.azure.datalake.analytics.job.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The statistics information for resource usage.
 */
public class ResourceUsageStatistics {
    /**
     * The average value.
     */
    @JsonProperty(value = "average", access = JsonProperty.Access.WRITE_ONLY)
    private Double average;

    /**
     * The minimum value.
     */
    @JsonProperty(value = "minimum", access = JsonProperty.Access.WRITE_ONLY)
    private Long minimum;

    /**
     * The maximum value.
     */
    @JsonProperty(value = "maximum", access = JsonProperty.Access.WRITE_ONLY)
    private Long maximum;

    /**
     * Get the average value.
     *
     * @return the average value
     */
    public Double average() {
        return this.average;
    }

    /**
     * Get the minimum value.
     *
     * @return the minimum value
     */
    public Long minimum() {
        return this.minimum;
    }

    /**
     * Get the maximum value.
     *
     * @return the maximum value
     */
    public Long maximum() {
        return this.maximum;
    }

}
/**
 * babysitter-server-prototype API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


export interface CommandExecutionData { 
    commandExecutionId?: string;
    commandSourceId?: string;
    commandSourceName?: string;
    commandId?: string;
    commandRun?: boolean;
    commandCompleted?: boolean;
    exitCode?: number;
    errorText?: string;
    startTime?: string;
    endTime?: string;
}

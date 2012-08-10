@echo off
rem ###############################################
rem ## MDM Patch Tool
rem ## usage: patchtool D:\Patch_TMDM_V5
rem ## Please make sure all the updated files are under D:\Patch_TMDM_V5\todeploy folder
rem ###############################################
java -cp patchtool.jar; org.talend.mdm.patchtool.PatchTool %1
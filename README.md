# 폴더 구조

```markdown
├── 📂 batch
│ ├── SyncIndexDataBatchJob
├── 📂 config
│ ├── AppConfig.java # 앱 config 처리
│ ├── QuerydslConfig
│
├── 📂 dto
│ ├── 📂 command - 서비스 요청값
│ │ ├── ExportCsvCommand.java
│ │ ├── GetNewIndexDataCommand.java
│ │ ├── IndexInfoDto.java
│ │ ├── SyncJobDto.java
│ ├── 📂 request
│ │ ├── IndexDataCreateRequest.java
│ │ ├── IndexDataRequest.java
│ │ ├── StockMarketIndexRequest.java
│ │ ├── SyncJobRequest.java
│ ├── 📂 response
│ │ ├── CursorPageResponseIndexDataDto.java
│ │ ├── CursorPageResponseSyncConfigResponse.java
│ │ ├── SyncJobResponse.java
│ │ ├── SyncResponse.java
│ ├── 📂 result - 서비스 반환값
│ │ ├── CursorPageResponseIndexDataDto.java
│ │ ├── CursorPageResponseSyncConfigResponse.java
│ │ ├── SyncJobResponse.java
│ │ ├── SyncResponse.java
│ ├── CursorPageResponseIndexInfoDto.java
│ ├── CursorPageResponseSyncConfigDto.java
│ ├── CursorPageResponseSyncJobDto.java
│ ├── PatchSyncConfigCommand.java
│ ├── SyncConfigDto.java
│ ├── SyncJobDto.java
│
├── 📂 exception
│ ├── 📂 handler
│ │ ├── GlobalExceptionHandler.java
│
├── 📂 mapper
│ ├── 📂 SyncConfigMapper
│ │ ├── SyncConfigMapper.java
│ │ ├── SyncJobMapper.java
│ ├── IndexDataMapper.java
│
├── 📂 controller
│ ├── IndexInfoController.java
│ ├── IndexDataController.java
│
├── 📂 domain
│ ├── 📂 entity
│ │ ├── 📂 base
│ │ │ ├── BaseEntity.java
│ │ │ ├── BaseUpdatableEntity.java
│ │ ├── IndexInfo
│ │ ├── IndexData
│ │ ├── SyncJob
│ ├── 📂 enums
│ │ ├── IndexDataSortDirection.java
│ │ ├── SourceType.java
│ │ ├── SyncConfigSoredField.java
│ │ ├── SyncJobSortedField.java
│
├── 📂 repository
│ ├── 📂 impl
│ │ ├── IndexDataRespositoryImpl.java
│ │ ├── IndexInfoQueryRepositoryImpl.java
│ │ ├── SyncConfigQueryRepositoryImpl.java
│ │ ├── SyncJobQueryRepositoryImpl.java
│ ├── 📂 indexData
│ │ ├── IndexDataRepositoryCustom.java
│ │ ├── IndexDataRepositoryCustomImpl.java
│ │ ├── IndexPerfomanceProjection.java
│ ├── IndexDataQueryRepository.java
│ ├── IndexDataRepository.java
│ ├── IndexInfoQueryRepository.java
│ ├── IndexInfoRepository.java
│ ├── SyncConfigQueryRepository.java
│ ├── SyncConfigRepository.java
│ ├── SncJobQueryRepository.java
│ ├── SyncJobRepository.java
│
├── 📂 service
│ ├── 📂 impl
│ │ ├── BatchindexDataServiceImpl.java
│ │ ├── BatchIndexInfoServiceImpl.java
│ │ ├── SyncJobServiceImpl.java
│ ├── IndexDataService.java
│ ├── IndexInfoService.javaa
│ ├── SyncIndexInfoService.java
│ ├── SyncJobService.java
│
├── 📂 util
│ ├── OpenApiUtil.java
│ ├── StockDateUtil.java
│
├── FindexApplication.java
```
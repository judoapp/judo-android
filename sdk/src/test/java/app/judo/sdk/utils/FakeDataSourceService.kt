package app.judo.sdk.utils

import app.judo.sdk.core.services.DataSourceService

internal class FakeDataSourceService : DataSourceService {

    override suspend fun getData(
        url: String,
        headers: Map<String, String>
    ): DataSourceService.Result {
        return DataSourceService.Result.Success(
            body = TestJSON.data_source_experience
        )
    }

    override suspend fun putData(
        url: String,
        headers: Map<String, String>,
        body: String?
    ): DataSourceService.Result {
        return DataSourceService.Result.Success(
            body = TestJSON.data_source_experience
        )
    }

    override suspend fun postData(
        url: String,
        headers: Map<String, String>,
        body: String?
    ): DataSourceService.Result {
        return DataSourceService.Result.Success(
            body = TestJSON.data_source_experience
        )
    }
}

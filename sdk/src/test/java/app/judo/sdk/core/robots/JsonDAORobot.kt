package app.judo.sdk.core.robots

import app.judo.sdk.core.data.JsonDAO
import app.judo.sdk.core.data.JsonDAOImpl

internal class JsonDAORobot : AbstractTestRobot() {

    lateinit var dao: JsonDAO

    val dummyJson = """{
  "userA": {
    "name": "Bob",
    "friends": [
      "userB"
    ]
  },
  "userB": {
    "name": "Bobo",
    "friends": [
      "userB"
    ]
  },
  "pets": [
    {
      "name": "DigDog",
      "owner": "userA"
    },
    {
      "name": "CatManDo",
      "owner": "userB"
    }
  ]
}""".trimMargin()

    override fun onSetUp() {
        super.onSetUp()

        dao = JsonDAOImpl(
            json = dummyJson
        )

    }
}

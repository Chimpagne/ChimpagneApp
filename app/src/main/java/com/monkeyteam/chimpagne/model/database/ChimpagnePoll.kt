package com.monkeyteam.chimpagne.model.database

import androidx.collection.ArrayMap
import java.util.UUID

typealias ChimpagnePollId = String

typealias ChimpagnePollOptionId = String

data class ChimpagnePoll(
    val id: ChimpagnePollId = UUID.randomUUID().toString(),
    val title: String = "",
    val query: String = "",
    val options: Map<ChimpagnePollOptionId, ChimpagnePollOption> = emptyMap(),
    val votes: Map<ChimpagneAccountUID, ChimpagnePollOptionId> = emptyMap(),
) {
  private fun getVotesPerOptions(): Map<ChimpagnePollOptionId, List<ChimpagneAccountUID>> {
    val votesPerOptions: MutableMap<ChimpagnePollOptionId, List<ChimpagneAccountUID>> = ArrayMap()
    options.keys.forEach { optionId -> votesPerOptions[optionId] = emptyList() }
    votes.forEach { (accountUID, optionId) ->
      votesPerOptions[optionId] = votesPerOptions[optionId]!!.plus(accountUID)
    }
    return votesPerOptions
  }

  fun getNumberOfVotesPerOption(): Map<ChimpagnePollOptionId, Int> {
    return getVotesPerOptions().mapValues { entry -> entry.value.size }
  }
}

data class ChimpagnePollOption(
    val id: ChimpagnePollOptionId = UUID.randomUUID().toString(),
    val optionText: String = "",
)

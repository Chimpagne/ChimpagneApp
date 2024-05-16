package com.monkeyteam.chimpagne.model.database

import java.util.UUID

typealias ChimpagnePollId = String

typealias ChimpagnePollOption = String

typealias ChimpagnePollOptionListIndex = Int

data class ChimpagnePoll(
    val id: ChimpagnePollId = UUID.randomUUID().toString(),
    val title: String = "",
    val query: String = "",
    // OPTIONS STRINGS MUST BE DISTINCT
    val options: List<ChimpagnePollOption> = emptyList(),
    val votes: Map<ChimpagneAccountUID, ChimpagnePollOptionListIndex> = emptyMap(),
) {
  private fun getVotesPerOptions(): List<List<ChimpagneAccountUID>> {
    val votesPerOptions: ArrayList<List<ChimpagneAccountUID>> = arrayListOf()
    options.forEach { _ -> votesPerOptions.add(emptyList()) }
    votes.forEach { (accountUID, optionIndex) ->
      votesPerOptions[optionIndex] = votesPerOptions[optionIndex].plus(accountUID)
    }
    return votesPerOptions.toList()
  }

  fun getNumberOfVotesPerOption(): List<Int> {
    return getVotesPerOptions().map { it.size }
  }
}

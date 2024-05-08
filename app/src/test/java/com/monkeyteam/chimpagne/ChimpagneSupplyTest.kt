import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import java.util.UUID
import org.junit.Assert.*
import org.junit.Test

class ChimpagneSupplyTest {

  @Test
  fun `test default values`() {
    val supply = ChimpagneSupply()
    assertNotNull(supply.id)
    assertEquals("", supply.description)
    assertEquals(0, supply.quantity)
    assertEquals("", supply.unit)
    assertTrue(supply.assignedTo.isEmpty())
  }

  @Test
  fun `test custom values`() {
    val id = UUID.randomUUID().toString()
    val description = "Champagne"
    val quantity = 10
    val unit = "bottles"
    val assignedTo = mapOf("guest1" to true, "guest2" to false)
    val supply = ChimpagneSupply(id, description, quantity, unit, assignedTo)
    assertEquals(id, supply.id)
    assertEquals(description, supply.description)
    assertEquals(quantity, supply.quantity)
    assertEquals(unit, supply.unit)
    assertEquals(assignedTo, supply.assignedTo)
  }

  @Test
  fun `test assignedList`() {
    val assignedTo = mapOf("guest1" to true, "guest2" to false)
    val supply = ChimpagneSupply(assignedTo = assignedTo)
    assertEquals(setOf("guest1", "guest2"), supply.assignedList())
  }

  @Test
  fun `test toString with unit`() {
    val description = "Champagne"
    val quantity = 10
    val unit = "bottles"
    val supply = ChimpagneSupply(description = description, quantity = quantity, unit = unit)
    assertEquals("$description $quantity $unit", supply.toString())
  }

  @Test
  fun `test toString without unit`() {
    val description = "Champagne"
    val quantity = 10
    val supply = ChimpagneSupply(description = description, quantity = quantity)
    assertEquals("$quantity $description", supply.toString())
  }
}

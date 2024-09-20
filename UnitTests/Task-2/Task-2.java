import org.junit.Test;
import org.junit.Before;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UnitTests {
 // Place the specific unit tests here for the current weeks assignment
 // Take the already made tests and make more by:
 // plugging the current weeks instructions and the correct answer
 // into ChatGPT (Currently 4o) to generate Junit tests
 @Test
 public void testParameterizedConstructor() {
     Indamon glassey = new Indamon("Glassey", 10, 5, 5, false);
     assertEquals("Glassey", glassey.getName());
     assertEquals(10, glassey.getHp());
     assertEquals(5, glassey.getAttack());
     assertEquals(5, glassey.getDefense());
     assertFalse(glassey.isFainted());  // Check fainted status
 }

 @Test
 public void testSettersAndGetters() {
     Indamon indamon = new Indamon("Flamey", 15, 7, 4, false);
     indamon.setName("Blazey");
     indamon.setHp(20);
     indamon.setAttack(10);
     indamon.setDefense(6);
     indamon.setFainted(true);

     assertEquals("Blazey", indamon.getName());
     assertEquals(20, indamon.getHp());
     assertEquals(10, indamon.getAttack());
     assertEquals(6, indamon.getDefense());
     assertTrue(indamon.isFainted());  // Check fainted status is true
 }

 @Test
 public void testAttack() {
     Indamon attacker = new Indamon("Attacker", 20, 10, 3, false);
     Indamon defender = new Indamon("Defender", 30, 8, 5, false);

     attacker.attack(defender);

     // Attack damage = attacker's attack / defender's defense
     int expectedDamage = attacker.getAttack() / defender.getDefense();
     assertEquals(30 - expectedDamage, defender.getHp());
 }

 @Test
 public void testFaintingCondition() {
     Indamon attacker = new Indamon("Strong", 20, 50, 5, false);
     Indamon defender = new Indamon("Weak", 10, 5, 1, false);

     attacker.attack(defender);

     assertTrue(defender.getHp() <= 0);
     defender.setFainted(true);
     assertTrue(defender.isFainted());  // Confirm fainted status
 }
}


     
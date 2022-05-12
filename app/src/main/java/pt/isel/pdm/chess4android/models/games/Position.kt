package pt.isel.pdm.chess4android.models.games

import kotlin.math.abs


class Position(var x: Int, var y: Int) {
    /**
     * Method equals and hashcode were overridden because two positions with same x and y need to be equal.
     * Both of the methods were create with the help of IntelliJ.
     * */


    fun isPositionInBetween(pos1: Position, pos2: Position): Boolean {
        if (x == pos1.x && x == pos2.x) // Check vertically
            return (pos1.y > y && y > pos2.y) || (pos1.y < y && y < pos2.y)
        if (y == pos1.y && y == pos2.y) // Check horizontally
            return (pos1.x > x && x > pos2.x) || (pos1.x < x && x < pos2.x)
        if (abs(x - pos1.x) == abs(y - pos1.y) && abs(x - pos2.x) == abs(y - pos2.y))
            return ((pos1.y > y && y > pos2.y) || (pos1.y < y && y < pos2.y)) && (
                    (pos1.x > x && x > pos2.x) || (pos1.x < x && x < pos2.x)
                    )
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        /**
         * The reason why 31 was used can be seen in the following excerpt from the book Joshua Bloch's Effective Java:
         *    The value 31 was chosen because it is an odd prime.
         *    If it were even and the multiplication overflowed, information would be lost, as multiplication by 2 is equivalent to shifting.
         *    The advantage of using a prime is less clear, but it is traditional.
         *    A nice property of 31 is that the multiplication can be replaced by a shift and a subtraction for better performance: 31 * i == (i << 5) - i.
         *    Modern VMs do this sort of optimization automatically.
         * */
        return 31 * x + y
    }
}
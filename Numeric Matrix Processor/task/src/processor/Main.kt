package processor

import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    while (true) {
        print("""
            1. Add matrices
            2. Multiply matrix by a constant
            3. Multiply matrices
            4. Transpose matrix
            5. Calculate a determinant
            6. Inverse matrix
            0. Exit
            Your choice: """.trimIndent())
        when (scanner.nextInt()) {
            1 -> println ("The result is:\n" +
                    (Matrix.scan(scanner, "first").plus(Matrix.scan(scanner, "second"))?:
                    "The operation cannot be performed."))
            2 -> println("The result is:\n${Matrix.scan(scanner).scale(scanner.nextDouble())}")

            3 -> println (Matrix.scan(scanner, "first").multiply(Matrix.scan(scanner, "second"))?:
                "The operation cannot be performed.")
            4 -> { print("""
                1. Main diagonal
                2. Side diagonal
                3. Vertical line
                4. Horizontal line 
                Your choice: """.trimIndent())
                val axis = scanner.nextInt()
                if (axis in 1..4) println("The result is:\n${Matrix.scan(scanner).transpose(axis)}")
            }
            5 -> println(Matrix.scan(scanner).det())
            6 -> println("The result is:\n" + (Matrix.scan(scanner).inverse()?: "This matrix doesn't have an inverse."))
            0 -> break
        }
    }
}

class Matrix(val rows: Int, val cols: Int) {
    val a: Array<Array<Double>> = Array<Array<Double>>(rows) { Array<Double>(cols) {0.0} }

    fun plus(other: Matrix): Matrix? {
        if(rows != other.rows || cols != other.cols) return null
        val matrix = Matrix(rows, cols)
        for (r in 0 until rows) for(c in 0 until cols) matrix.a[r][c] = a[r][c] + other.a[r][c]
        return matrix
    }

    fun scale(scalar: Double): Matrix {
        val matrix = Matrix(rows, cols)
        for (r in 0 until rows) for(c in 0 until cols) matrix.a[r][c] = a[r][c] * scalar
        return matrix
    }

    fun multiply(other: Matrix): Matrix? {
        if(cols != other.rows) return null
        val matrix = Matrix(rows, other.cols)
        for (r in 0 until rows) for (c in 0 until other.cols)
            for (i in 0 until cols) matrix.a[r][c] += a[r][i] * other.a[i][c]
        return matrix
    }

    fun det(): Double {
        if (rows == 2 && cols == 2) return a[0][0] * a[1][1] - a[0][1] * a[1][0]
        var det = 0.0
        for (c in 0 until cols) det += a[0][c] * cofactor(0, c)
        return det
    }

    fun cofactor(row: Int, col: Int): Double = submatrix(row, col).det() * (((row + col) % 2) * -2 + 1)

    fun submatrix(row: Int, col: Int): Matrix {
        val matrix = Matrix(rows - 1, cols -1)
        for (r in 0 until matrix.rows) for(c in 0 until matrix.cols)
            matrix.a[r][c] = a[if(r < row) r else r + 1][if(c < col) c else c + 1]
        return matrix
    }

    fun transpose(axis: Int): Matrix {
        val matrix: Matrix
        when (axis) {
            1 -> { matrix = Matrix(cols, rows)  //Main diagonal
                for (r in 0 until rows) for (c in 0 until cols) matrix.a[c][r] = a[r][c] }
            2 -> { matrix = Matrix(cols, rows)  // Side diagonal
                for (r in 0 until rows) for (c in 0 until cols) matrix.a[c][r] = a[rows - r - 1][cols - c - 1] }
            3 -> { matrix = Matrix(rows, cols)  // Vertical line
                for (r in 0 until rows) for (c in 0 until cols) matrix.a[r][c] = a[r][cols - c - 1] }
            else -> { matrix = Matrix(rows, cols) // 4. Horizontal line
                for (r in 0 until rows) for (c in 0 until cols) matrix.a[r][c] = a[rows - r - 1][c] }
        }
        return matrix
    }

    fun inverse(): Matrix? {
        val det = det()
        if (rows != cols || det == 0.0) return null
        val matrix = Matrix(rows, cols)
        for (r in 0 until rows) for(c in 0 until cols) matrix.a[c][r] = cofactor(r, c) / det
        return matrix
    }

    override fun toString() = a.joinToString("\n") { row ->
        row.joinToString(" ") {"%6.2f".format(it)} }

    companion object {
        fun scan(scanner: Scanner, name: String = ""): Matrix {
            print("Enter size of $name matrix: ")
            val matrix = Matrix(scanner.nextInt(), scanner.nextInt())
            println("Enter $name matrix: ")
            for (r in 0 until matrix.rows) for(c in 0 until matrix.cols) matrix.a[r][c] = scanner.nextDouble()
            return matrix
        }
    }
}
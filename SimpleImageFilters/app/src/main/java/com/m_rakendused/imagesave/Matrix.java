package com.m_rakendused.imagesave;

// very simple naive matrix math, nothing fancy
public class Matrix
{
    public int rows;
    public int columns;
    public int[][] data;

    public Matrix(int rows, int cols)
    {
        this.rows = rows;
        this.columns = cols;
        this.data = new int[rows][cols];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                this.data[i][j] = 0;
            }
        }
    }

    public void Multiply(Matrix m)
    {
        for (int i = 0; i < this.rows; i++)
        {
            for (int j = 0; j < this.columns; j++)
            {
                this.data[i][j] *= m.data[i][j];
            }
        }
    }

    public static Matrix MatrixMultiply(Matrix m1, Matrix m2)
    {
        // Matrixes have to be the same size
        Matrix result = new Matrix(m1.rows, m1.columns);
        for (int i = 0; i < result.rows; i++)
        {
            for (int j = 0; j < result.columns; j++)
            {
                result.data[i][j] = m1.data[i][j] * m2.data[i][j];
            }
        }
        return result;
    }

    public void Multiply(float n)
    {
        for (int i = 0; i < this.rows; i++)
        {
            for (int j = 0; j < this.columns; j++)
            {
                this.data[i][j] *= n;
            }
        }
    }

    public float MatrixSum ()
    {
        float result = 0;
        for (int i = 0; i < this.rows; i++)
        {
            for (int j = 0; j < this.columns; j++)
            {
                result += this.data[i][j];
            }
        }
        return result;
    }
}


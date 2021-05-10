package com.m_rakendused.imagesave;

import android.util.Pair;

import java.util.Stack;

public class Canny {

    Stack<int[]> strongPixels = new Stack<int[]>();

    // sobel operation for detecting big color changes (x and y axis)
    public Pair<Matrix, Matrix> ApplySobelOperator (Matrix matrix)
    {
        Matrix sobelGradientX = new Matrix(3, 3);
        Matrix sobelGradientY = new Matrix(3, 3);
        sobelGradientX.data = new int[][] { { -1, 0, 1}, { -2, 0, 2}, {-1, 0, 1} };
        sobelGradientY.data = new int[][] { { 1, 2, 1}, { 0, 0, 0}, {-1, -2, -1} };

        Matrix sobelGradients = new Matrix(matrix.rows - 1, matrix.columns - 1);
        Matrix sobelDirections = new Matrix(matrix.rows - 1, matrix.columns - 1);

        for (int y = 1; y < matrix.rows - 1; y++)
        {
            for (int x = 1; x < matrix.columns - 1; x++)
            {
                Matrix pixelsToView = new Matrix(3, 3);
                pixelsToView.data = new int[][] {
                { matrix.data[y - 1][x - 1], matrix.data[y - 1][x], matrix.data[y - 1][x + 1] },
                { matrix.data[y][x - 1], matrix.data[y][x], matrix.data[y][x + 1] },
                { matrix.data[y + 1][x - 1], matrix.data[y + 1][x], matrix.data[y + 1][x + 1] }
            };
                Matrix sobelResultX = Matrix.MatrixMultiply(pixelsToView, sobelGradientX);
                Matrix sobelResultY = Matrix.MatrixMultiply(pixelsToView, sobelGradientY);
                int sobelGradientValue = (int)Math.sqrt(Math.pow((double)sobelResultX.MatrixSum(), 2) + Math.pow((double)sobelResultY.MatrixSum(), 2));
                int sobelDirectionValue = GetAngleRegion((float)Math.abs(((180 / Math.PI) * Math.atan(sobelResultY.MatrixSum() / sobelResultX.MatrixSum()))));

                if (sobelGradientValue > 255)
                {
                    sobelGradients.data[y][x] = 255;
                } else
                {
                    sobelGradients.data[y][x] = sobelGradientValue;
                }
                sobelDirections.data[y][x] = sobelDirectionValue;
            }
        }

        return Pair.create(sobelGradients, sobelDirections);
    }

    // using sobelGradients and sobelDirections to thin out the edges
    public Matrix ApplyNonMaximumSupression (Matrix gradients, Matrix directions)
    {
        Matrix result = new Matrix(gradients.rows, gradients.columns);

        for (int y = 1; y < gradients.rows - 1; y++)
        {
            for (int x = 1; x < gradients.columns - 1; x++)
            {
                if (gradients.data[y][x] >= GetPixelByDirection(gradients, (int)directions.data[y][x], x, y, false) && gradients.data[y][x] > GetPixelByDirection(gradients, (int)directions.data[y][x], x, y, true))
                {
                    result.data[y][x] = gradients.data[y][x];
                } else
                {
                    result.data[y][x] = 0;
                }
            }
        }

        return result;
    }

    // making strong edges 255 and weak edges 0
    public Matrix ApplyDoubleTresholding (Matrix gradients)
    {
        gradients =  RemoveOuterPixels(gradients);

        for (int y = 0; y < gradients.rows; y++)
        {
            for (int x = 0; x < gradients.columns; x++)
            {
                if (gradients.data[y][x] > 255 * 0.35f)
                {
                    gradients.data[y][x] = 255;
                    strongPixels.push(new int[] { y, x });
                } else if (gradients.data[y][x] < 255 * 0.1f)
                {
                    gradients.data[y][x] = 0;
                }
            }
        }

        return gradients;
    }

    // if some pixels are connected to strong pixels, then we will set them to 255 as well
    public Matrix ApplyEdgeTracking (Matrix gradients)
    {
        while (strongPixels.size() > 0)
        {
            //System.Diagnostics.Debug.WriteLine("value: " + strongPixels.Count);
            int strongValue = 0;

            int[] coordinates = strongPixels.pop();
            int y = coordinates[0];
            int x = coordinates[1];

            if (gradients.data[y][x + 1] > strongValue && gradients.data[y][x + 1] != 255)
            {
                gradients.data[y][x + 1] = 255;
                strongPixels.push(new int[] { y, x + 1 });
            }
            if (gradients.data[y][x - 1] > strongValue && gradients.data[y][x - 1] != 255)
            {
                gradients.data[y][x - 1] = 255;
                strongPixels.push(new int[] { y, x - 1 });
            }
            if (gradients.data[y + 1][x] > strongValue && gradients.data[y + 1][x] != 255)
            {
                gradients.data[y + 1][x] = 255;
                strongPixels.push(new int[] { y + 1, x });
            }
            if (gradients.data[y - 1][x] > strongValue && gradients.data[y - 1][x] != 255)
            {
                gradients.data[y - 1][x] = 255;
                strongPixels.push(new int[] { y - 1, x });
            }
            if (gradients.data[y + 1][x + 1] > strongValue && gradients.data[y + 1][x + 1] != 255)
            {
                gradients.data[y + 1][x + 1] = 255;
                strongPixels.push(new int[] { y + 1, x + 1 });
            }
            if (gradients.data[y - 1][x + 1] > strongValue && gradients.data[y - 1][x + 1] != 255)
            {
                gradients.data[y - 1][x + 1] = 255;
                strongPixels.push(new int[] { y - 1, x + 1 });
            }
            if (gradients.data[y + 1][x - 1] > strongValue && gradients.data[y + 1][x - 1] != 255)
            {
                gradients.data[y + 1][x - 1] = 255;
                strongPixels.push(new int[] { y + 1, x - 1 });
            }
            if (gradients.data[y - 1][x - 1] > strongValue && gradients.data[y - 1][x - 1] != 255)
            {
                gradients.data[y - 1][x - 1] = 255;
                strongPixels.push(new int[] { y - 1, x - 1 });
            }
        }

        return gradients;
    }

    // setting some weak edges to 0
    public Matrix ApplyCleaning (Matrix gradients)
    {
        for (int y = 0; y < gradients.rows; y++)
        {
            for (int x = 0; x < gradients.columns; x++)
            {
                if (gradients.data[y][x] < 255)
                {
                    gradients.data[y][x] = 0;
                } /**else
             {
             gradients.data[y][x] = 1;
             }**/
            }
        }

        return gradients;
    }

    public int GetAngleRegion (float angle)
    {
        if (angle < 22.5f || angle > 157.5f)
        {
            return 0;
        } else if (angle >= 22.5f && angle < 67.5f)
        {
            return 45;
        } else if (angle >= 67.5f && angle < 112.5f)
        {
            return 90;
        } else
        {
            return 135;
        }
    }

    public float GetPixelByDirection (Matrix pixels, int direction, int x, int y, boolean oppositeDir)
    {
        if (!oppositeDir)
        {
            switch (direction)
            {
                case 0:
                    return pixels.data[y][x + 1];
                case 45:
                    return pixels.data[y - 1][x + 1];
                case 90:
                    return pixels.data[y - 1][x];
                default:
                    return pixels.data[y - 1][x - 1];
            }
        } else
        {
            switch (direction)
            {
                case 0:
                    return pixels.data[y][x - 1];
                case 45:
                    return pixels.data[y + 1][x - 1];
                case 90:
                    return pixels.data[y + 1][x];
                default:
                    return pixels.data[y + 1][x + 1];
            }
        }
    }

    public Matrix RemoveOuterPixels (Matrix matrix)
    {
        for (int y = 0; y < matrix.rows; y++)
        {
            for (int x = 0; x < matrix.columns; x++)
            {
                matrix.data[y][x] = 0;
                if (y != 0 && y != matrix.rows)
                {
                    matrix.data[y][matrix.columns - 1] = 0;
                    break;
                }
            }
        }

        return matrix;
    }
}

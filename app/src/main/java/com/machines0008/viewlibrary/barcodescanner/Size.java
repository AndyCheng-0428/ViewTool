package com.machines0008.viewlibrary.barcodescanner;

import java.util.Objects;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
public class Size implements Comparable<Size> {
    public final int width;
    public final int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Size rotate() {
        return new Size(height, width);
    }

    public Size scale(int n, int d) {
        return new Size(width * n / d, height * n / d);
    }

    public Size scaleFit(Size into) {
        if (width * into.height >= into.width * height) {
            return new Size(into.width, height * into.width / width);
        } else {
            return new Size(width * into.height / height, into.height);
        }
    }

    public Size scaleCrop(Size into) {
        if (width * into.height <= into.width * height) {
            return new Size(into.width, height * into.width / width);
        } else {
            return new Size(width * into.height / height, into.height);
        }
    }

    public boolean fitsIn(Size other) {
        return width <= other.width && height <= other.height;
    }

    @Override
    public int compareTo(Size o) {
        int aPixels = this.height * this.width;
        int bPixels = o.height * o.width;
        return bPixels < aPixels ? 1 : bPixels == aPixels ? 0 : -1;
    }

    public String toString() {
        return width + "x" + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return width == size.width && height == size.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
package com.sciencesquad.health.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Point implements Serializable {
	private static final long serialVersionUID = -7526147553632397385L;
	public static int BYTE_LENGTH = 16;

	public static Point fromByteArray(final byte[] data) {
		final byte[] temp = new byte[8];
		double x;
		double y;
		System.arraycopy(data, 0, temp, 0, 8);
		x = toDouble(temp);
		System.arraycopy(data, 8, temp, 0, 8);
		y = toDouble(temp);
		return new Point(x, y);
	}

	private static byte[] toByte(final double data) {
		return toByte(Double.doubleToRawLongBits(data));
	}

	private static byte[] toByte(final long data) {
		return new byte[]{(byte) (data >> 56 & 0xff), (byte) (data >> 48 & 0xff),
				(byte) (data >> 40 & 0xff), (byte) (data >> 32 & 0xff), (byte) (data >> 24 & 0xff),
				(byte) (data >> 16 & 0xff), (byte) (data >> 8 & 0xff), (byte) (data >> 0 & 0xff),};
	}

	public static byte[] toByteArray(final Point point) {
		final byte[] bytes = new byte[16];
		System.arraycopy(toByte(point.x), 0, bytes, 0, 8);
		System.arraycopy(toByte(point.y), 0, bytes, 8, 8);
		return bytes;
	}

	private static double toDouble(final byte[] data) {
		if (data == null || data.length != 8) {
			return 0x0;
		}
		return Double.longBitsToDouble(toLong(data));
	}

	private static long toLong(final byte[] data) {
		if (data == null || data.length != 8) {
			return 0x0;
		}
		return (long) (0xff & data[0]) << 56 | (long) (0xff & data[1]) << 48
				| (long) (0xff & data[2]) << 40 | (long) (0xff & data[3]) << 32
				| (long) (0xff & data[4]) << 24 | (long) (0xff & data[5]) << 16
				| (long) (0xff & data[6]) << 8 | (long) (0xff & data[7]) << 0;
	}

	public double x;

	public double y;

	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public static List<Point> convertToNew(List<Point> oldList) {
		ArrayList<Point> newList = new ArrayList<>(oldList.size());
		for (Point oldPoint : oldList) {
			newList.add(new Point(oldPoint.x, oldPoint.y));
		}
		return newList;
	}
}

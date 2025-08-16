package com.ronreynolds.tools.gcode;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class Point3DTest {
    @Test
    void testEverything() {
        // null IS allowed for x, y, z values to handle cases where the value wasn't specified
        Point3D nullPoint = Point3D.builder().build();
        assertThat(nullPoint).isNotNull()
                .satisfies(p -> assertThat(p.getX()).isNull())
                .satisfies(p -> assertThat(p.getY()).isNull())
                .satisfies(p -> assertThat(p.getZ()).isNull())
                ;
        Point3D point000 = Point3D.builder().x(0f).y(0f).z(0f).build();
        Point3D point123 = Point3D.builder().x(1f).y(2f).z(3f).build();
        assertThat(point000).isNotNull()
                .satisfies(p -> assertThat(p.getX()).isZero())
                .satisfies(p -> assertThat(p.getY()).isZero())
                .satisfies(p -> assertThat(p.getZ()).isZero())
                .satisfies(p -> assertThat(p).isEqualTo(point000))
                .satisfies(p -> assertThat(p).isNotEqualTo(point123))
        ;
        assertThat(point123).isNotNull()
                .satisfies(p -> assertThat(p.getX()).isEqualTo(1))
                .satisfies(p -> assertThat(p.getY()).isEqualTo(2))
                .satisfies(p -> assertThat(p.getZ()).isEqualTo(3))
                .satisfies(p -> assertThat(p).isEqualTo(point123))
                .satisfies(p -> assertThat(p).isNotEqualTo(point000))
        ;
        Point3D otherPoint123 = Point3D.builder().x(1f).y(2f).z(3f).build();
        assertThat(otherPoint123).isEqualTo(point123).isNotEqualTo(point000);
        assertThat(otherPoint123.hashCode()).isEqualTo(point123.hashCode());
        System.out.println(point123);
    }
}
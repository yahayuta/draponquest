#!/usr/bin/env python3

# Generate Alefgard-like map for Drapon Quest

import random

# Map size
WIDTH = 64
HEIGHT = 64

# Tile types
WATER = 0
SAND = 1
GRASS = 2
FOREST = 3

map_data = [[WATER for _ in range(WIDTH)] for _ in range(HEIGHT)]

# Define regions
for i in range(HEIGHT):
    for j in range(WIDTH):
        if i < 3 or i > 60:
            map_data[i][j] = WATER
        elif i < 15:
            # Mountains, but with some passes
            if random.random() < 0.1:
                map_data[i][j] = GRASS
            else:
                map_data[i][j] = FOREST
        elif i < 40:
            # Plains
            map_data[i][j] = GRASS
        elif i < 50:
            # Desert peninsula
            if 15 <= j <= 48:
                map_data[i][j] = SAND
            else:
                map_data[i][j] = WATER
        else:
            map_data[i][j] = WATER

# Add some forests in plains
for i in range(20, 31):
    for j in range(20, 31):
        if random.random() < 0.3:
            map_data[i][j] = FOREST

# Add a lake
for i in range(25, 31):
    for j in range(35, 41):
        map_data[i][j] = WATER

# Add some water in desert for variety
for i in range(40, 50):
    for j in range(15, 49):
        if random.random() < 0.05:
            map_data[i][j] = WATER

# Add some forests in south
for i in range(45, 50):
    for j in range(20, 45):
        if random.random() < 0.1:
            map_data[i][j] = FOREST

# Print the Java array
print("  private static int mapDataField[][]={")
for i in range(HEIGHT):
    print("    {" + ",".join(str(x) for x in map_data[i]) + "},")
print("  };")
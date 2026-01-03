
import os

WIDTH = 128
HEIGHT = 128

# Tile Constants matching fieldMapData.java
TILE_SEA = 0
TILE_SAND = 1
TILE_STEPPE = 2
TILE_FOREST = 3
TILE_SHOP = 4
TILE_PLAINS = 5
TILE_MOUNTAIN = 6
TILE_TOWN = 7
TILE_CASTLE = 8
TILE_BRIDGE = 9
TILE_SWAMP = 10
TILE_WALL = 11
TILE_FLOOR = 12
TILE_CAVE = 13
TILE_CHEST = 14

# Initialize map with Sea
grid = [[TILE_SEA for _ in range(WIDTH)] for _ in range(HEIGHT)]

def fill_rect(r1, c1, r2, c2, tile):
    for r in range(r1, r2):
        for c in range(c1, c2):
            if 0 <= r < HEIGHT and 0 <= c < WIDTH:
                grid[r][c] = tile

# Helper for approximate shaping
def fill_circle(center_r, center_c, radius, tile):
    for r in range(center_r - radius, center_r + radius):
        for c in range(center_c - radius, center_c + radius):
            if (r - center_r)**2 + (c - center_c)**2 <= radius**2:
                if 0 <= r < HEIGHT and 0 <= c < WIDTH:
                    grid[r][c] = tile

# --- LANDMASS GENERATION (Approximating Alefgard) ---

# 1. Western Landmass (The large "C" shape containing Tantegel/Garinham/Domdora)
# Main vertical spine
fill_rect(10, 10, 110, 45, TILE_PLAINS)
# Top bulge (Garinham)
fill_rect(10, 10, 35, 70, TILE_PLAINS)
# Bottom bulge (Domdora)
fill_rect(80, 10, 110, 60, TILE_PLAINS)
# Connection towards center (Tantegel area)
fill_rect(40, 40, 70, 70, TILE_PLAINS)


# 2. Northeastern Landmass (Kol)
fill_rect(15, 80, 50, 120, TILE_PLAINS)
# Extension South (Eastern Coast towards Rimuldar) - Extend further to row 80
fill_rect(50, 90, 80, 115, TILE_PLAINS)

# 3. Southeastern Island (Rimuldar) - Start closer at row 81
fill_rect(81, 85, 115, 120, TILE_PLAINS) # Main island
# Connection bridge area is separate

# 4. Central Island (Charlock) - surrounded by water/poison
fill_rect(58, 58, 72, 72, TILE_MOUNTAIN) # Dark island
fill_rect(60, 60, 70, 70, TILE_SWAMP) # Poison swamp center

# 5. Southern Landmass (Cantlin area)
fill_rect(90, 60, 120, 90, TILE_PLAINS)

# --- TERRAIN FEATURES ---

# Mountains around Garinham (NW)
fill_rect(12, 12, 25, 30, TILE_MOUNTAIN)
# Mountains North/Central
fill_rect(15, 50, 25, 65, TILE_MOUNTAIN)

# Desert at Domdora (SW)
fill_rect(85, 15, 105, 40, TILE_SAND)

# Mountains separating Rimuldar
fill_circle(98, 102, 8, TILE_MOUNTAIN)

# Mountains near Kol
fill_rect(20, 90, 40, 110, TILE_MOUNTAIN)

# Forests - scattered patches
fill_circle(30, 110, 5, TILE_FOREST) # Near Kol
fill_circle(20, 20, 4, TILE_FOREST) # Garinham
fill_circle(60, 50, 5, TILE_FOREST) # Southwest of Tantegel

# Swamp (Poison) PATCHES
fill_rect(45, 110, 50, 118, TILE_SWAMP) # North of Rimuldar/South of Kol
fill_rect(100, 75, 110, 85, TILE_SWAMP) # Near Cantlin

# --- CITIES & CASTLES ---
# Tantegel Castle (Approx central-west)
grid[56][56] = TILE_CASTLE
# Brecconary Town (Next to Tantegel)
grid[56][59] = TILE_TOWN

# Garinham (NW)
grid[18][20] = TILE_TOWN

# Kol (NE)
grid[25][110] = TILE_TOWN

# Rimuldar (SE Island)
grid[100][105] = TILE_TOWN

# Domdora (SW Desert - destroyed town in DQ1, but let's mark it)
grid[95][30] = TILE_TOWN

# Cantlin (South Fortress)
grid[110][80] = TILE_TOWN

# Charlock Castle (Center Island)
grid[65][65] = TILE_CASTLE

# --- BRIDGES ---
# North Bridge (West to East)
# Extend West land to meet bridge
fill_rect(22, 70, 28, 79, TILE_PLAINS) 
grid[25][79] = TILE_BRIDGE # connects to col 80

grid[80][95] = TILE_BRIDGE # South to Rimuldar (at gap row 80)
grid[60][55] = TILE_BRIDGE # To Charlock? Actually requires Rainbow Drop. Let's put a bridge for access if wanted, or leave gap.
# In DQ1, you use a rainbow drop to bridge. I'll add a 'broken' bridge or just sea. 
# Let's add the bridge tile as a placeholder for where it appears.
grid[55][55] = TILE_BRIDGE

# Caves
grid[20][70] = TILE_CAVE # Erdrick's Tablet cave
grid[45][115] = TILE_CAVE # Staff of Rain shrine area
grid[115][115] = TILE_CAVE # Holy Shrine

# Fill gaps between rects with Sea to refine shapes? 
# The rect approach is blocky. Let's just leave it as 'close enough' for now.

# OUTPUT
output_path = os.path.join("src", "main", "resources", "alefgard_data.txt")
os.makedirs(os.path.dirname(output_path), exist_ok=True)

with open(output_path, "w") as f:
    for row in grid:
        line = " ".join(str(tile) for tile in row)
        f.write(line + "\n")

print(f"Map data generated at {output_path}")

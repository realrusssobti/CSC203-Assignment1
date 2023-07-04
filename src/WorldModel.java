import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import processing.core.PImage;

import java.util.*;
final class WorldModel
{
   public int numRows;
   public int numCols;
   public Background background[][];
   public Entity occupancy[][];
   public Set<Entity> entities;

   public static final int ORE_REACH = 1;


   
   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }
   
   public boolean isOccupied( Point pos)
   {
      return withinBounds(pos) &&
         getOccupancyCell(pos) != null;
   }

   public boolean withinBounds( Point pos)
   {
      return pos.y >= 0 && pos.y < this.numRows &&
         pos.x >= 0 && pos.x < this.numCols;
   }


   public Entity getOccupancyCell(Point pos)
   {
      return this.occupancy[pos.y][pos.x];
   }
   
   public Optional<Entity> findNearest(Point pos,
      EntityKind kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : this.entities)
      {
         if (entity.kind == kind)
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }

   public static Optional<Entity> nearestEntity(List<Entity> entities,
      Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = distanceSquared(nearest.position, pos);

         for (Entity other : entities)
         {
            int otherDistance = distanceSquared(other.position, pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   public static int distanceSquared(Point p1, Point p2)
   {
      int deltaX = p1.x - p2.x;
      int deltaY = p1.y - p2.y;

      return deltaX * deltaX + deltaY * deltaY;
   }

   public void removeEntity( Entity entity)
   {
      removeEntityAt( entity.position);
   }

   public void removeEntityAt( Point pos)
   {
      if (withinBounds(pos)
         && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell( pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.position = new Point(-1, -1);
         this.entities.remove(entity);
         setOccupancyCell( pos, null);
      }
   }

   public void setOccupancyCell( Point pos,
      Entity entity)
   {
      this.occupancy[pos.y][pos.x] = entity;
   }


   public Optional<Point> findOpenAround( Point pos)
   {
      for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++)
      {
         for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds( newPt) &&
               !isOccupied( newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public Optional<Entity> getOccupant( Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell( pos));
      }
      else
      {
         return Optional.empty();
      }
   }


   public void moveEntity( Entity entity, Point pos)
   {
      Point oldPos = entity.position;
      if (withinBounds( pos) && !pos.equals(oldPos))
      {
         setOccupancyCell( oldPos, null);
         removeEntityAt( pos);
         setOccupancyCell( pos, entity);
         entity.position = pos;
      }
   }

 /*
      Assumes that there is no entity currently occupying the
      intended destination cell.
   */
  public void addEntity( Entity entity)
  {
     if (withinBounds( entity.position))
     {
        setOccupancyCell( entity.position, entity);
        entities.add(entity);
     }
  }
  public void setBackground(Point pos,
  Background background)
{
  if (withinBounds( pos))
  {
     setBackgroundCell( pos, background);
  }
}
public  Background getBackgroundCell( Point pos)
{
   return background[pos.y][pos.x];
}

public  void setBackgroundCell(Point pos,
   Background background)
{
   this.background[pos.y][pos.x] = background;
}
public  void tryAddEntity( Entity entity)
{
   if (isOccupied( entity.position))
   {
      // arguably the wrong type of exception, but we are not
      // defining our own exceptions yet
      throw new IllegalArgumentException("position occupied");
   }

   addEntity( entity);
}
public  Optional<PImage> getBackgroundImage(
      Point pos)
   {
      if (withinBounds( pos))
      {
         return Optional.of(Functions.getCurrentImage(getBackgroundCell( pos)));
      }
      else
      {
         return Optional.empty();
      }
   }

}

/**
 * This file is part of ancat.
 * 
 * ancat is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * ancat is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ancat. If not, see <http://www.gnu.org/licenses/>.
 */
package ancat.ui;

/**
 * @author alunkeit
 * 
 */
public interface LayoutChangeListener
{
  public static final int FR_LAYOUT = 1;

  public static final int CIRCLE_LAYOUT = 2;

  public static final int KK_LAYOUT = 3;

  public static final int ISOM_LAYOUT = 4;

  public static final int SPRING_LAYOUT = 5;

  public void layoutChangeRequest( LayoutEvent layout );

}

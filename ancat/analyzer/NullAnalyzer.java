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
package ancat.analyzer;

import java.io.IOException;

/**
 * @author alunkeit
 * 
 */
public class NullAnalyzer extends Analyzer
{

  /*
   * (non-Javadoc)
   * 
   * @see ancat.analyzer.Analyzer#init(java.lang.String)
   */
  @Override
  public void init( String file )
  {
    _logger.debug( "NullAnalyzer::init()" );
  }

  /*
   * (non-Javadoc)
   * 
   * @see ancat.analyzer.Analyzer#doOutput()
   */
  @Override
  public void doOutput() throws Exception
  {
    _logger.debug( "NullAnalyzer::doOutput()" );
  }

  /*
   * (non-Javadoc)
   * 
   * @see ancat.analyzer.Analyzer#getOutput()
   */
  @Override
  public StringBuffer getOutput() throws Exception
  {
    _logger.debug( "NullAnalyzer::getOutput()" );

    return new StringBuffer();
  }

  /*
   * (non-Javadoc)
   * 
   * @see ancat.analyzer.Analyzer#analyze()
   */
  @Override
  public void analyze() throws IOException,
      AnalyzerException,
      TransformationError
  {
    _logger.debug( "NullAnalyzer::analyze()" );
  }

}

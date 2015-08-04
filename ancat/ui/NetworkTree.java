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

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

/**
 * @author alunkeit
 * 
 *         Implementation of a TreeView control on the left side of the UI
 */
public class NetworkTree extends JTree implements TreeSelectionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7569619811160502403L;

	private Logger _logger = Logger.getRootLogger();

	private DefaultMutableTreeNode _networkConfig;

	private MainFrame _mainFrame;

	public NetworkTree(MainFrame frame)
	{
		super(new DefaultMutableTreeNode("network definition"));

		_mainFrame = frame;

		setRootVisible(true);

		setExpandsSelectedPaths(true);

		addTreeSelectionListener(this);
	}

	public void setNetworkConfiguration(File f)
	{
		_logger.debug("NetworkTree: setNetworkFile : " + f.getAbsolutePath());

		_networkConfig = new DefaultMutableTreeNode(f.getName());

		((DefaultMutableTreeNode) getModel().getRoot()).add(_networkConfig);

		setSelectionPath(new TreePath(_networkConfig.getPath()));

		updateView();
	}

	public void addNetwork(String name)
	{
		DefaultMutableTreeNode n = new DefaultMutableTreeNode(name);

		_networkConfig.add(n);

		setSelectionPath(new TreePath(n.getPath()));

		updateView();
	}

	protected void updateView()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{

				// expandAll();
				updateUI();
			}
		});

	}

	protected void expandAll()
	{
		int i = this.getRowCount();

		int j = 0;

		while (j < i)
		{
			this.expandRow(j);
			j += 1;
			i = getRowCount();
		}
	}

	public void activate()
	{

	}

	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		_logger.debug("tree selection has changed");

		if (e.getSource() != getModel().getRoot() && e.getSource() != _networkConfig)
		{
			TreePath path = e.getNewLeadSelectionPath();

			if (path != null)
			{
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();

				_logger.debug("selected node: " + selectedNode);

				_mainFrame.setActiveView((String) selectedNode.getUserObject());
			}
		}

	}
}

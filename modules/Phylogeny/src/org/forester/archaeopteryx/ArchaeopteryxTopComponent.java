/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.forester.archaeopteryx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import org.forester.archaeopteryx.tools.InferenceManager;
import org.forester.archaeopteryx.tools.ProcessPool;
import org.forester.archaeopteryx.tools.ProcessRunning;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNodeI;
import org.forester.phylogeny.data.NodeVisualization;
import org.forester.util.ForesterConstants;
import org.forester.util.ForesterUtil;
import org.openide.windows.TopComponent;

/**
 *
 * @author lucy
 */
public abstract class ArchaeopteryxTopComponent extends TopComponent implements ActionListener{
    // $Id:
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2008-2010 Christian M. Zmasek
// All rights reserved
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
//
// Contact: phylosoft @ gmail . com
// WWW: www.phylosoft.org/forester


    static final String       USE_MOUSEWHEEL_SHIFT_TO_ROTATE          = "In this display type, use mousewheel + Shift to rotate [or A and S]";
    static final String       PHYLOXML_REF_TOOL_TIP                   = Constants.PHYLOXML_REFERENCE;                                                                                                                                       //TODO //FIXME
    static final String       APTX_REF_TOOL_TIP                       = Constants.APTX_REFERENCE;
    private static final long serialVersionUID                        = 3655000897845508358L;
    final static Font         menu_font                               = new Font( Configuration.getDefaultFontFamilyName(),
                                                                                  Font.PLAIN,
                                                                                  10 );
    static final String       TYPE_MENU_HEADER                        = "Type";
    static final String       RECTANGULAR_TYPE_CBMI_LABEL             = "Rectangular";
    static final String       EURO_TYPE_CBMI_LABEL                    = "Euro Type";
    static final String       CURVED_TYPE_CBMI_LABEL                  = "Curved";
    static final String       TRIANGULAR_TYPE_CBMI_LABEL              = "Triangular";
    static final String       CONVEX_TYPE_CBMI_LABEL                  = "Convex";
    static final String       ROUNDED_TYPE_CBMI_LABEL                 = "Rounded";
    static final String       UNROOTED_TYPE_CBMI_LABEL                = "Unrooted (alpha)";                                                                                                                                                 //TODO
    static final String       CIRCULAR_TYPE_CBMI_LABEL                = "Circular (alpha)";                                                                                                                                                 //TODO
    static final String       OPTIONS_HEADER                          = "Options";
    static final String       SEARCH_SUBHEADER                        = "Search:";
    static final String       DISPLAY_SUBHEADER                       = "Display:";
    static final String       SEARCH_TERMS_ONLY_LABEL                 = "Match Complete Terms Only";
    static final String       SEARCH_CASE_SENSITIVE_LABEL             = "Case Sensitive";
    static final String       INVERSE_SEARCH_RESULT_LABEL             = "Negate Result";
    static final String       DISPLAY_BRANCH_LENGTH_VALUES_LABEL      = "Display Branch Length Values";
    static final String       DISPLAY_SCALE_LABEL                     = "Display Scale";
    static final String       NON_LINED_UP_CLADOGRAMS_LABEL           = "Non-Lined Up Cladograms";
    static final String       UNIFORM_CLADOGRAMS_LABEL                = "Total Node Sum Dependent Cladograms";
    static final String       LABEL_DIRECTION_LABEL                   = "Radial Labels";
    static final String       LABEL_DIRECTION_TIP                     = "To use radial node labels in radial and unrooted display types";
    static final String       SCREEN_ANTIALIAS_LABEL                  = "Antialias";
    static final String       COLOR_LABELS_LABEL                      = "Colorize Labels Same as Parent Branch";
    static final String       BG_GRAD_LABEL                           = "Background Color Gradient";
    static final String       DISPLAY_NODE_BOXES_LABEL                = "Show Node Shapes";
    static final String       SHOW_OVERVIEW_LABEL                     = "Show Overview";
    static final String       FONT_SIZE_MENU_LABEL                    = "Font Size";
    static final String       NONUNIFORM_CLADOGRAMS_LABEL             = "External Node Sum Dependent Cladograms";
    static final String       SHOW_DOMAIN_LABELS_LABEL                = "Show Domain Labels";
    static final String       COLOR_LABELS_TIP                        = "To use parent branch colors for node labels as well, need to turn off taxonomy dependent colorization and turn on branch colorization for this to become apparent";
    static final String       ABBREV_SN_LABEL                         = "Abbreviate Scientific Taxonomic Names";
    static final String       TAXONOMY_COLORIZE_NODE_SHAPES_LABEL     = "Colorize Node Shapes According to Taxonomy";
    static final String       CYCLE_NODE_SHAPE_LABEL                  = "Cycle Node Shapes";
    static final String       CYCLE_NODE_FILL_LABEL                   = "Cycle Node Fill Type";
    static final String       CHOOSE_NODE_SIZE_LABEL                  = "Choose Node Shape Size";
    static final String       SHOW_CONF_STDDEV_LABEL                  = "Show Confidence Standard Deviations";
    static final String       USE_BRACKETS_FOR_CONF_IN_NH_LABEL       = "Use Brackets for Confidence Values";
    static final String       USE_INTERNAL_NAMES_FOR_CONF_IN_NH_LABEL = "Use Internal Node Names for Confidence Values";
    JMenuBar                  _jmenubar;
    JMenu                     _file_jmenu;
    JMenu                     _tools_menu;
    JMenu                     _view_jmenu;
    JMenu                     _options_jmenu;
    JMenu                     _font_size_menu;
    JMenu                     _help_jmenu;
    JMenuItem[]               _load_phylogeny_from_webservice_menu_items;
    // file menu:
    JMenuItem                 _open_item;
    JMenuItem                 _open_url_item;
    JMenuItem                 _save_item;
    JMenuItem                 _save_all_item;
    JMenuItem                 _close_item;
    JMenuItem                 _exit_item;
    JMenuItem                 _new_item;
    // tools menu:
    JMenuItem                 _midpoint_root_item;
    JMenuItem                 _taxcolor_item;
    JMenuItem                 _confcolor_item;
    JMenuItem                 _color_rank_jmi;
    JMenuItem                 _infer_common_sn_names_item;
    JMenuItem                 _collapse_species_specific_subtrees;
    JMenuItem                 _collapse_below_threshold;                                                                                                                                                                                    //TODO implememt me
    JMenuItem                 _obtain_detailed_taxonomic_information_jmi;
    JMenuItem                 _obtain_detailed_taxonomic_information_deleting_jmi;
    JMenuItem                 _obtain_uniprot_seq_information_jmi;
    JMenuItem                 _move_node_names_to_tax_sn_jmi;
    JMenuItem                 _move_node_names_to_seq_names_jmi;
    JMenuItem                 _extract_tax_code_from_node_names_jmi;
    // font size menu:
    JMenuItem                 _super_tiny_fonts_item;
    JMenuItem                 _tiny_fonts_item;
    JMenuItem                 _small_fonts_item;
    JMenuItem                 _medium_fonts_item;
    JMenuItem                 _large_fonts_item;
    // options menu:
    // _  screen and print
    JMenuItem                 _choose_font_mi;
    JMenuItem                 _switch_colors_mi;
    JCheckBoxMenuItem         _label_direction_cbmi;
    // _  screen display
    JCheckBoxMenuItem         _screen_antialias_cbmi;
    JCheckBoxMenuItem         _background_gradient_cbmi;
    JRadioButtonMenuItem      _non_lined_up_cladograms_rbmi;
    JRadioButtonMenuItem      _uniform_cladograms_rbmi;
    JRadioButtonMenuItem      _ext_node_dependent_cladogram_rbmi;
    JCheckBoxMenuItem         _show_branch_length_values_cbmi;
    JCheckBoxMenuItem         _show_scale_cbmi;                                                                                                                                                                                             //TODO fix me
    JCheckBoxMenuItem         _show_overview_cbmi;
    JCheckBoxMenuItem         _show_domain_labels;
    JCheckBoxMenuItem         _abbreviate_scientific_names;
    JCheckBoxMenuItem         _color_labels_same_as_parent_branch;
    JMenuItem                 _overview_placment_mi;
    JMenuItem                 _choose_minimal_confidence_mi;
    JCheckBoxMenuItem         _show_default_node_shapes_cbmi;
    JMenuItem                 _cycle_node_shape_mi;
    JMenuItem                 _cycle_node_fill_mi;
    JMenuItem                 _choose_node_size_mi;
    JCheckBoxMenuItem         _taxonomy_colorize_node_shapes_cbmi;
    JCheckBoxMenuItem         _show_confidence_stddev_cbmi;
    // _  print
    JCheckBoxMenuItem         _graphics_export_visible_only_cbmi;
    JCheckBoxMenuItem         _antialias_print_cbmi;
    JCheckBoxMenuItem         _print_black_and_white_cbmi;
    JCheckBoxMenuItem         _print_using_actual_size_cbmi;
    JCheckBoxMenuItem         _graphics_export_using_actual_size_cbmi;
    JMenuItem                 _print_size_mi;
    JMenuItem                 _choose_pdf_width_mi;
    // _  parsing
    JCheckBoxMenuItem         _internal_number_are_confidence_for_nh_parsing_cbmi;
    JCheckBoxMenuItem         _extract_pfam_style_tax_codes_cbmi;
    JCheckBoxMenuItem         _replace_underscores_cbmi;
    JCheckBoxMenuItem         _use_brackets_for_conf_in_nh_export_cbmi;
    JCheckBoxMenuItem         _use_internal_names_for_conf_in_nh_export_cbmi;
    // _  search
    JCheckBoxMenuItem         _search_case_senstive_cbmi;
    JCheckBoxMenuItem         _search_whole_words_only_cbmi;
    JCheckBoxMenuItem         _inverse_search_result_cbmi;
    // type menu:
    JMenu                     _type_menu;
    JCheckBoxMenuItem         _rectangular_type_cbmi;
    JCheckBoxMenuItem         _triangular_type_cbmi;
    JCheckBoxMenuItem         _curved_type_cbmi;
    JCheckBoxMenuItem         _convex_type_cbmi;
    JCheckBoxMenuItem         _euro_type_cbmi;
    JCheckBoxMenuItem         _rounded_type_cbmi;
    JCheckBoxMenuItem         _unrooted_type_cbmi;
    JCheckBoxMenuItem         _circular_type_cbmi;
    // view as text menu:
    JMenuItem                 _view_as_NH_item;
    JMenuItem                 _view_as_NHX_item;
    JMenuItem                 _view_as_XML_item;
    JMenuItem                 _view_as_nexus_item;
    JMenuItem                 _display_basic_information_item;
    // help menu:
    JMenuItem                 _about_item;
    JMenuItem                 _help_item;
    JMenuItem                 _website_item;
    JMenuItem                 _phyloxml_website_item;
    JMenuItem                 _phyloxml_ref_item;
    JMenuItem                 _aptx_ref_item;
    // process menu:
    JMenu                     _process_menu;
    // Handy pointers to child components:
    MainPanel                 _mainpanel;
    Container                 _contentpane;
    TextFrame                 _textframe;
    Configuration             _configuration;
    JMenuItem                 _remove_branch_color_item;
    Options                   _options;
    InferenceManager          _inference_manager;
    final ProcessPool         _process_pool;

    ArchaeopteryxTopComponent() {
        _process_pool = ProcessPool.createInstance();
    }

    public ProcessPool getProcessPool() {
        return _process_pool;
    }

    /**
     * Action performed.
     */
    public void actionPerformed( final ActionEvent e ) {
        final Object o = e.getSource();
        boolean is_applet = false;
        JApplet applet = null;
        if ( getCurrentTreePanel() != null ) {
            is_applet = getCurrentTreePanel().isApplet();
            if ( is_applet ) {
                applet = getCurrentTreePanel().obtainApplet();
            }
        }
        if ( o == _open_url_item ) {
            readPhylogeniesFromURL();
        }
        else if ( o == _exit_item ) {
            close();
        }
        else if ( o == _taxcolor_item ) {
            taxColor();
        }
        else if ( o == _confcolor_item ) {
            confColor();
        }
        else if ( o == _color_rank_jmi ) {
            colorRank();
        }
        else if ( o == _infer_common_sn_names_item ) {
            if ( isSubtreeDisplayed() ) {
                return;
            }
            if ( getCurrentTreePanel() != null ) {
                getCurrentTreePanel().inferCommonPartOfScientificNames();
            }
        }
        else if ( o == _collapse_species_specific_subtrees ) {
            if ( isSubtreeDisplayed() ) {
                return;
            }
            if ( getCurrentTreePanel() != null ) {
                getCurrentTreePanel().collapseSpeciesSpecificSubtrees();
            }
        }
        else if ( o == _remove_branch_color_item ) {
            if ( isSubtreeDisplayed() ) {
                return;
            }
            removeBranchColors();
        }
        else if ( o == _midpoint_root_item ) {
            if ( isSubtreeDisplayed() ) {
                return;
            }
            midpointRoot();
        }
        else if ( o == _switch_colors_mi ) {
            switchColors();
        }
        else if ( o == _display_basic_information_item ) {
            displayBasicInformation();
        }
        else if ( o == _view_as_NH_item ) {
            viewAsNH();
        }
        else if ( o == _view_as_NHX_item ) {
            viewAsNHX();
        }
        else if ( o == _view_as_XML_item ) {
            viewAsXML();
        }
        else if ( o == _view_as_nexus_item ) {
            viewAsNexus();
        }
        else if ( o == _super_tiny_fonts_item ) {
            if ( getCurrentTreePanel() != null ) {
                getCurrentTreePanel().setSuperTinyFonts();
                getCurrentTreePanel().repaint();
            }
        }
        else if ( o == _tiny_fonts_item ) {
            if ( getCurrentTreePanel() != null ) {
                getCurrentTreePanel().setTinyFonts();
                getCurrentTreePanel().repaint();
            }
        }
        else if ( o == _small_fonts_item ) {
            if ( getCurrentTreePanel() != null ) {
                getCurrentTreePanel().setSmallFonts();
                getCurrentTreePanel().repaint();
            }
        }
        else if ( o == _medium_fonts_item ) {
            if ( getCurrentTreePanel() != null ) {
                getCurrentTreePanel().setMediumFonts();
                getCurrentTreePanel().repaint();
            }
        }
        else if ( o == _large_fonts_item ) {
            if ( getCurrentTreePanel() != null ) {
                getCurrentTreePanel().setLargeFonts();
                getCurrentTreePanel().repaint();
            }
        }
        else if ( o == _choose_font_mi ) {
            chooseFont();
        }
        else if ( o == _choose_minimal_confidence_mi ) {
            chooseMinimalConfidence();
        }
        else if ( o == _choose_node_size_mi ) {
            chooseNodeSize( getOptions(), this );
        }
        else if ( o == _overview_placment_mi ) {
            org.forester.archaeopteryx.MainFrame.cycleOverview( getOptions(), getCurrentTreePanel() );
        }
        else if ( o == _cycle_node_fill_mi ) {
            org.forester.archaeopteryx.MainFrame.cycleNodeFill( getOptions(), getCurrentTreePanel() );
        }
        else if ( o == _cycle_node_shape_mi ) {
            org.forester.archaeopteryx.MainFrame.cycleNodeShape( getOptions(), getCurrentTreePanel() );
        }
        else if ( o == _screen_antialias_cbmi ) {
            updateOptions( getOptions() );
            updateScreenTextAntialias( getMainPanel().getTreePanels() );
        }
        else if ( o == _background_gradient_cbmi ) {
            updateOptions( getOptions() );
        }
        else if ( o == _show_domain_labels ) {
            updateOptions( getOptions() );
        }
        else if ( o == _abbreviate_scientific_names ) {
            updateOptions( getOptions() );
        }
        else if ( o == _color_labels_same_as_parent_branch ) {
            updateOptions( getOptions() );
        }
        else if ( o == _show_default_node_shapes_cbmi ) {
            updateOptions( getOptions() );
        }
        else if ( o == _taxonomy_colorize_node_shapes_cbmi ) {
            updateOptions( getOptions() );
        }
        else if ( o == _non_lined_up_cladograms_rbmi ) {
            updateOptions( getOptions() );
            showWhole();
        }
        else if ( o == _uniform_cladograms_rbmi ) {
            updateOptions( getOptions() );
            showWhole();
        }
        else if ( o == _ext_node_dependent_cladogram_rbmi ) {
            updateOptions( getOptions() );
            showWhole();
        }
        else if ( o == _search_case_senstive_cbmi ) {
            updateOptions( getOptions() );
            getMainPanel().getControlPanel().search();
        }
        else if ( o == _search_whole_words_only_cbmi ) {
            updateOptions( getOptions() );
            getMainPanel().getControlPanel().search();
        }
        else if ( o == _inverse_search_result_cbmi ) {
            updateOptions( getOptions() );
            getMainPanel().getControlPanel().search();
        }
        else if ( o == _show_scale_cbmi ) {
            updateOptions( getOptions() );
        }
        else if ( o == _show_branch_length_values_cbmi ) {
            updateOptions( getOptions() );
        }
        else if ( o == _show_confidence_stddev_cbmi ) {
            updateOptions( getOptions() );
        }
        else if ( o == _use_brackets_for_conf_in_nh_export_cbmi ) {
            if ( _use_brackets_for_conf_in_nh_export_cbmi.isSelected() ) {
                _use_internal_names_for_conf_in_nh_export_cbmi.setSelected( false );
            }
            updateOptions( getOptions() );
        }
        else if ( o == _use_internal_names_for_conf_in_nh_export_cbmi ) {
            if ( _use_internal_names_for_conf_in_nh_export_cbmi.isSelected() ) {
                _use_brackets_for_conf_in_nh_export_cbmi.setSelected( false );
            }
            updateOptions( getOptions() );
        }
        else if ( o == _label_direction_cbmi ) {
            updateOptions( getOptions() );
        }
        else if ( o == _show_overview_cbmi ) {
            updateOptions( getOptions() );
            if ( getCurrentTreePanel() != null ) {
                getCurrentTreePanel().updateOvSizes();
            }
        }
        else if ( ( o == _rectangular_type_cbmi ) || ( o == _triangular_type_cbmi ) || ( o == _curved_type_cbmi )
                || ( o == _convex_type_cbmi ) || ( o == _euro_type_cbmi ) || ( o == _rounded_type_cbmi )
                || ( o == _unrooted_type_cbmi ) || ( o == _circular_type_cbmi ) ) {
            typeChanged( o );
        }
        else if ( o == _about_item ) {
            about();
        }
        else if ( o == _help_item ) {
            help( getConfiguration().getWebLinks() );
        }
        else if ( o == _website_item ) {
            try {
                AptxUtil.openWebsite( Constants.APTX_WEB_SITE, is_applet, applet );
            }
            catch ( final IOException e1 ) {
                ForesterUtil.printErrorMessage( Constants.PRG_NAME, e1.toString() );
            }
        }
        else if ( o == _phyloxml_website_item ) {
            try {
                AptxUtil.openWebsite( Constants.PHYLOXML_WEB_SITE, is_applet, applet );
            }
            catch ( final IOException e1 ) {
                ForesterUtil.printErrorMessage( Constants.PRG_NAME, e1.toString() );
            }
        }
        else if ( o == _aptx_ref_item ) {
            try {
                AptxUtil.openWebsite( Constants.APTX_REFERENCE_URL, is_applet, applet );
            }
            catch ( final IOException e1 ) {
                ForesterUtil.printErrorMessage( Constants.PRG_NAME, e1.toString() );
            }
        }
        else if ( o == _phyloxml_ref_item ) {
            try {
                AptxUtil.openWebsite( Constants.PHYLOXML_REFERENCE_URL, is_applet, applet );
            }
            catch ( final IOException e1 ) {
                ForesterUtil.printErrorMessage( Constants.PRG_NAME, e1.toString() );
            }
        }
        else {
            if ( _load_phylogeny_from_webservice_menu_items != null ) {
                for( int i = 0; i < _load_phylogeny_from_webservice_menu_items.length; ++i ) {
                    if ( o == _load_phylogeny_from_webservice_menu_items[ i ] ) {
                        readPhylogeniesFromWebservice( i );
                    }
                }
            }
        }
        _contentpane.repaint();
    }

    boolean isSubtreeDisplayed() {
        if ( getCurrentTreePanel() != null ) {
            if ( getCurrentTreePanel().isCurrentTreeIsSubtree() ) {
                JOptionPane
                        .showMessageDialog( this,
                                            "This operation can only be performed on a complete tree, not on the currently displayed sub-tree only.",
                                            "Operation can not be exectuted on a sub-tree",
                                            JOptionPane.WARNING_MESSAGE );
                return true;
            }
        }
        return false;
    }

    void activateSaveAllIfNeeded() {
        if ( ( getMainPanel().getTabbedPane() != null ) && ( getMainPanel().getTabbedPane().getTabCount() > 1 ) ) {
            _save_all_item.setEnabled( true );
        }
        else {
            _save_all_item.setEnabled( false );
        }
    }

    void buildFileMenu() {
        _file_jmenu = createMenu( "File", getConfiguration() );
        _file_jmenu.add( _open_url_item = new JMenuItem( "Read tree from URL/webservice..." ) );
        _file_jmenu.addSeparator();
        _file_jmenu.add( _exit_item = new JMenuItem( "Exit" ) );
        customizeJMenuItem( _open_url_item );
        customizeJMenuItem( _exit_item );
        _jmenubar.add( _file_jmenu );
    }

    void buildFontSizeMenu() {
        _font_size_menu = createMenu( FONT_SIZE_MENU_LABEL, getConfiguration() );
        _font_size_menu.add( _super_tiny_fonts_item = new JMenuItem( "Super Tiny Fonts" ) );
        _font_size_menu.add( _tiny_fonts_item = new JMenuItem( "Tiny Fonts" ) );
        _font_size_menu.add( _small_fonts_item = new JMenuItem( "Small Fonts" ) );
        _font_size_menu.add( _medium_fonts_item = new JMenuItem( "Medium Fonts" ) );
        _font_size_menu.add( _large_fonts_item = new JMenuItem( "Large Fonts" ) );
        customizeJMenuItem( _super_tiny_fonts_item );
        customizeJMenuItem( _tiny_fonts_item );
        customizeJMenuItem( _small_fonts_item );
        customizeJMenuItem( _medium_fonts_item );
        customizeJMenuItem( _large_fonts_item );
        _jmenubar.add( _font_size_menu );
    }

    void buildHelpMenu() {
        _help_jmenu = createMenu( "Help", getConfiguration() );
        _help_jmenu.add( _help_item = new JMenuItem( "Help" ) );
        _help_jmenu.add( _website_item = new JMenuItem( "Archaeopteryx Home" ) );
        _aptx_ref_item = new JMenuItem( "Archaeopteryx Reference" );
        _help_jmenu.add( _phyloxml_website_item = new JMenuItem( "phyloXML Home" ) );
        _help_jmenu.add( _phyloxml_ref_item = new JMenuItem( "phyloXML Reference" ) );
        _help_jmenu.addSeparator();
        _help_jmenu.add( _about_item = new JMenuItem( "About" ) );
        customizeJMenuItem( _help_item );
        customizeJMenuItem( _website_item );
        customizeJMenuItem( _phyloxml_website_item );
        customizeJMenuItem( _aptx_ref_item );
        customizeJMenuItem( _phyloxml_ref_item );
        customizeJMenuItem( _about_item );
        _phyloxml_ref_item.setToolTipText( PHYLOXML_REF_TOOL_TIP );
        _aptx_ref_item.setToolTipText( APTX_REF_TOOL_TIP );
        _jmenubar.add( _help_jmenu );
    }

    public void updateProcessMenu() {
        // In general Swing is not thread safe.
        // See "Swing's Threading Policy".
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {
                doUpdateProcessMenu();
            }
        } );
    }

    private void doUpdateProcessMenu() {
        if ( _process_pool.size() > 0 ) {
            if ( _process_menu == null ) {
                _process_menu = createMenu( "", getConfiguration() );
                _process_menu.setForeground( Color.RED );
            }
            _process_menu.removeAll();
            final String text = "processes running: " + _process_pool.size();
            _process_menu.setText( text );
            _jmenubar.add( _process_menu );
            for( int i = 0; i < _process_pool.size(); ++i ) {
                final ProcessRunning p = _process_pool.getProcessByIndex( i );
                _process_menu.add( customizeJMenuItem( new JMenuItem( p.getName() + " [" + p.getStart() + "]" ) ) );
            }
        }
        else {
            if ( _process_menu != null ) {
                _process_menu.removeAll();
                _jmenubar.remove( _process_menu );
            }
        }
        _jmenubar.validate();
        _jmenubar.repaint();
        repaint();
    }

    void buildTypeMenu() {
        _type_menu = createMenu( TYPE_MENU_HEADER, getConfiguration() );
        _type_menu.add( _rectangular_type_cbmi = new JCheckBoxMenuItem( org.forester.archaeopteryx.MainFrame.RECTANGULAR_TYPE_CBMI_LABEL ) );
        _type_menu.add( _euro_type_cbmi = new JCheckBoxMenuItem( org.forester.archaeopteryx.MainFrame.EURO_TYPE_CBMI_LABEL ) );
        _type_menu.add( _rounded_type_cbmi = new JCheckBoxMenuItem( org.forester.archaeopteryx.MainFrame.ROUNDED_TYPE_CBMI_LABEL ) );
        _type_menu.add( _curved_type_cbmi = new JCheckBoxMenuItem( org.forester.archaeopteryx.MainFrame.CURVED_TYPE_CBMI_LABEL ) );
        _type_menu.add( _triangular_type_cbmi = new JCheckBoxMenuItem( org.forester.archaeopteryx.MainFrame.TRIANGULAR_TYPE_CBMI_LABEL ) );
        _type_menu.add( _convex_type_cbmi = new JCheckBoxMenuItem( org.forester.archaeopteryx.MainFrame.CONVEX_TYPE_CBMI_LABEL ) );
        _type_menu.add( _unrooted_type_cbmi = new JCheckBoxMenuItem( org.forester.archaeopteryx.MainFrame.UNROOTED_TYPE_CBMI_LABEL ) );
        _type_menu.add( _circular_type_cbmi = new JCheckBoxMenuItem( org.forester.archaeopteryx.MainFrame.CIRCULAR_TYPE_CBMI_LABEL ) );
        customizeCheckBoxMenuItem( _rectangular_type_cbmi, false );
        customizeCheckBoxMenuItem( _triangular_type_cbmi, false );
        customizeCheckBoxMenuItem( _euro_type_cbmi, false );
        customizeCheckBoxMenuItem( _rounded_type_cbmi, false );
        customizeCheckBoxMenuItem( _curved_type_cbmi, false );
        customizeCheckBoxMenuItem( _convex_type_cbmi, false );
        customizeCheckBoxMenuItem( _unrooted_type_cbmi, false );
        customizeCheckBoxMenuItem( _circular_type_cbmi, false );
        _unrooted_type_cbmi.setToolTipText( org.forester.archaeopteryx.MainFrame.USE_MOUSEWHEEL_SHIFT_TO_ROTATE );
        _circular_type_cbmi.setToolTipText( org.forester.archaeopteryx.MainFrame.USE_MOUSEWHEEL_SHIFT_TO_ROTATE );
        initializeTypeMenu( getOptions() );
        _jmenubar.add( _type_menu );
    }

    void buildViewMenu() {
        _view_jmenu = createMenu( "View", getConfiguration() );
        _view_jmenu.add( _display_basic_information_item = new JMenuItem( "Display Basic Information" ) );
        _view_jmenu.addSeparator();
        _view_jmenu.add( _view_as_XML_item = new JMenuItem( "View as phyloXML" ) );
        _view_jmenu.add( _view_as_NH_item = new JMenuItem( "View as Newick" ) );
        _view_jmenu.add( _view_as_NHX_item = new JMenuItem( "View as NHX" ) );
        _view_jmenu.add( _view_as_nexus_item = new JMenuItem( "View as Nexus" ) );
        customizeJMenuItem( _display_basic_information_item );
        customizeJMenuItem( _view_as_NH_item );
        customizeJMenuItem( _view_as_NHX_item );
        customizeJMenuItem( _view_as_XML_item );
        customizeJMenuItem( _view_as_nexus_item );
        _jmenubar.add( _view_jmenu );
    }

    private void chooseFont() {
        final FontChooser fc = new FontChooser();
        fc.setFont( getMainPanel().getTreeFontSet().getLargeFont() );
        fc.showDialog( this, "Select the Base Font" );
        getMainPanel().getTreeFontSet().setBaseFont( fc.getFont() );
    }

    private void chooseMinimalConfidence() {
        final String s = ( String ) JOptionPane
                .showInputDialog( this,
                                  "Please enter the minimum for confidence values to be displayed.\n"
                                          + "[current value: " + getOptions().getMinConfidenceValue() + "]\n",
                                  "Minimal Confidence Value",
                                  JOptionPane.QUESTION_MESSAGE,
                                  null,
                                  null,
                                  getOptions().getMinConfidenceValue() );
        if ( !ForesterUtil.isEmpty( s ) ) {
            boolean success = true;
            double m = 0.0;
            final String m_str = s.trim();
            if ( !ForesterUtil.isEmpty( m_str ) ) {
                try {
                    m = Double.parseDouble( m_str );
                }
                catch ( final Exception ex ) {
                    success = false;
                }
            }
            else {
                success = false;
            }
            if ( success && ( m >= 0.0 ) ) {
                getOptions().setMinConfidenceValue( m );
            }
        }
    }

    static void chooseNodeSize( final Options options, final Component parent ) {
        final String s = ( String ) JOptionPane.showInputDialog( parent,
                                                                 "Please enter the default size for node shapes.\n"
                                                                         + "[current value: "
                                                                         + options.getDefaultNodeShapeSize() + "]\n",
                                                                 "Node Shape Size",
                                                                 JOptionPane.QUESTION_MESSAGE,
                                                                 null,
                                                                 null,
                                                                 options.getDefaultNodeShapeSize() );
        if ( !ForesterUtil.isEmpty( s ) ) {
            boolean success = true;
            double m = 0.0;
            final String m_str = s.trim();
            if ( !ForesterUtil.isEmpty( m_str ) ) {
                try {
                    m = Double.parseDouble( m_str );
                }
                catch ( final Exception ex ) {
                    success = false;
                }
            }
            else {
                success = false;
            }
            if ( success && ( m >= 0.0 ) ) {
                final short size = ForesterUtil.roundToShort( m );
                if ( size >= 0.0 ) {
                    options.setDefaultNodeShapeSize( size );
                }
            }
        }
    }

//    void close() {
//        removeTextFrame();
//        if ( _mainpanel != null ) {
//            _mainpanel.terminate();
//        }
//        if ( _contentpane != null ) {
//            _contentpane.removeAll();
//        }
//        setVisible( false );
//        dispose();
//    }

    void confColor() {
        if ( _mainpanel.getCurrentTreePanel() != null ) {
            _mainpanel.getCurrentTreePanel().confColor();
        }
    }

    void colorRank() {
        if ( _mainpanel.getCurrentTreePanel() != null ) {
            final String[] ranks = AptxUtil.getAllPossibleRanks();
            final String rank = ( String ) JOptionPane
                    .showInputDialog( this,
                                      "What rank should the colorization be based on",
                                      "Rank Selection",
                                      JOptionPane.QUESTION_MESSAGE,
                                      null,
                                      ranks,
                                      null );
            if ( !ForesterUtil.isEmpty( rank ) ) {
                _mainpanel.getCurrentTreePanel().colorRank( rank );
            }
        }
    }

    void customizeCheckBoxMenuItem( final JCheckBoxMenuItem item, final boolean is_selected ) {
        if ( item != null ) {
            item.setFont( org.forester.archaeopteryx.MainFrame.menu_font );
            if ( !getConfiguration().isUseNativeUI() ) {
                item.setBackground( getConfiguration().getGuiMenuBackgroundColor() );
                item.setForeground( getConfiguration().getGuiMenuTextColor() );
            }
            item.setSelected( is_selected );
            item.addActionListener( this );
        }
    }

    JMenuItem customizeJMenuItem( final JMenuItem jmi ) {
        if ( jmi != null ) {
            jmi.setFont( org.forester.archaeopteryx.MainFrame.menu_font );
            if ( !getConfiguration().isUseNativeUI() ) {
                jmi.setBackground( getConfiguration().getGuiMenuBackgroundColor() );
                jmi.setForeground( getConfiguration().getGuiMenuTextColor() );
            }
            jmi.addActionListener( this );
        }
        return jmi;
    }

    void customizeRadioButtonMenuItem( final JRadioButtonMenuItem item, final boolean is_selected ) {
        if ( item != null ) {
            item.setFont( org.forester.archaeopteryx.MainFrame.menu_font );
            if ( !getConfiguration().isUseNativeUI() ) {
                item.setBackground( getConfiguration().getGuiMenuBackgroundColor() );
                item.setForeground( getConfiguration().getGuiMenuTextColor() );
            }
            item.setSelected( is_selected );
            item.addActionListener( this );
        }
    }

    void exceptionOccuredDuringOpenFile( final Exception e ) {
        try {
            _mainpanel.getCurrentTreePanel().setArrowCursor();
        }
        catch ( final Exception ex ) {
            // Do nothing.
        }
        JOptionPane.showMessageDialog( this,
                                       ForesterUtil.wordWrap( e.getLocalizedMessage(), 80 ),
                                       "Error during File|Open",
                                       JOptionPane.ERROR_MESSAGE );
    }

    void exceptionOccuredDuringSaveAs( final Exception e ) {
        try {
            _mainpanel.getCurrentTreePanel().setArrowCursor();
        }
        catch ( final Exception ex ) {
            // Do nothing.
        }
        JOptionPane.showMessageDialog( this, "Exception" + e, "Error during File|SaveAs", JOptionPane.ERROR_MESSAGE );
    }

    boolean GAndSDoHaveMoreThanOneSpeciesInComman( final Phylogeny gene_tree ) {
        if ( ( gene_tree == null ) || gene_tree.isEmpty() ) {
            JOptionPane.showMessageDialog( this,
                                           "Gene tree and species tree have no species in common.",
                                           "Error during SDI",
                                           JOptionPane.ERROR_MESSAGE );
            return false;
        }
        else if ( gene_tree.getNumberOfExternalNodes() < 2 ) {
            JOptionPane.showMessageDialog( this,
                                           "Gene tree and species tree have only one species in common.",
                                           "Error during SDI",
                                           JOptionPane.ERROR_MESSAGE );
            return false;
        }
        else {
            return true;
        }
    }

    public Configuration getConfiguration() {
        return _configuration;
    }

    TreePanel getCurrentTreePanel() {
        return getMainPanel().getCurrentTreePanel();
    }

    JMenu getHelpMenu() {
        return _help_jmenu;
    }

    JCheckBoxMenuItem getlabelDirectionCbmi() {
        return _label_direction_cbmi;
    }

    public MainPanel getMainPanel() {
        return _mainpanel;
    }

    JMenuBar getMenuBarOfMainFrame() {
        return _jmenubar;
    }

    public Options getOptions() {
        return _options;
    }

    void initializeTypeMenu( final Options options ) {
        setTypeMenuToAllUnselected();
        switch ( options.getPhylogenyGraphicsType() ) {
            case CONVEX:
                _convex_type_cbmi.setSelected( true );
                break;
            case CURVED:
                _curved_type_cbmi.setSelected( true );
                break;
            case EURO_STYLE:
                _euro_type_cbmi.setSelected( true );
                break;
            case ROUNDED:
                _rounded_type_cbmi.setSelected( true );
                break;
            case TRIANGULAR:
                _triangular_type_cbmi.setSelected( true );
                break;
            case UNROOTED:
                _unrooted_type_cbmi.setSelected( true );
                break;
            case CIRCULAR:
                _circular_type_cbmi.setSelected( true );
                break;
            default:
                _rectangular_type_cbmi.setSelected( true );
                break;
        }
    }

    void midpointRoot() {
        if ( _mainpanel.getCurrentTreePanel() != null ) {
            _mainpanel.getCurrentTreePanel().midpointRoot();
        }
    }

    abstract void readPhylogeniesFromURL();

    abstract void readPhylogeniesFromWebservice( final int i ); 
//    {
//        final UrlTreeReader reader = new UrlTreeReader( this, i );
//        new Thread( reader ).start();
//    }

    private void removeBranchColors() {
        if ( getMainPanel().getCurrentPhylogeny() != null ) {
            AptxUtil.removeBranchColors( getMainPanel().getCurrentPhylogeny() );
        }
    }

    void removeTextFrame() {
        if ( _textframe != null ) {
            _textframe.close();
            _textframe = null;
        }
    }

    void setConfiguration( final Configuration configuration ) {
        _configuration = configuration;
    }

    void setOptions( final Options options ) {
        _options = options;
    }

    void setInferenceManager( final InferenceManager i ) {
        _inference_manager = i;
    }

    public InferenceManager getInferenceManager() {
        return _inference_manager;
    }

    void setSelectedTypeInTypeMenu( final Options.PHYLOGENY_GRAPHICS_TYPE type ) {
        setTypeMenuToAllUnselected();
        switch ( type ) {
            case CIRCULAR:
                _circular_type_cbmi.setSelected( true );
                break;
            case CONVEX:
                _convex_type_cbmi.setSelected( true );
                break;
            case CURVED:
                _curved_type_cbmi.setSelected( true );
                break;
            case EURO_STYLE:
                _euro_type_cbmi.setSelected( true );
                break;
            case ROUNDED:
                _rounded_type_cbmi.setSelected( true );
                break;
            case RECTANGULAR:
                _rectangular_type_cbmi.setSelected( true );
                break;
            case TRIANGULAR:
                _triangular_type_cbmi.setSelected( true );
                break;
            case UNROOTED:
                _unrooted_type_cbmi.setSelected( true );
                break;
            default:
                throw new IllegalArgumentException( "unknown type: " + type );
        }
    }

    void setTypeMenuToAllUnselected() {
        _convex_type_cbmi.setSelected( false );
        _curved_type_cbmi.setSelected( false );
        _euro_type_cbmi.setSelected( false );
        _rounded_type_cbmi.setSelected( false );
        _triangular_type_cbmi.setSelected( false );
        _rectangular_type_cbmi.setSelected( false );
        _unrooted_type_cbmi.setSelected( false );
        _circular_type_cbmi.setSelected( false );
    }

    public void showWhole() {
        _mainpanel.getControlPanel().showWhole();
    }

    void switchColors() {
        final TreeColorSet colorset = _mainpanel.getTreeColorSet();
        final ColorSchemeChooser csc = new ColorSchemeChooser( getMainPanel(), colorset );
        csc.setVisible( true );
    }

    void taxColor() {
        if ( _mainpanel.getCurrentTreePanel() != null ) {
            _mainpanel.getCurrentTreePanel().taxColor();
        }
    }

    void typeChanged( final Object o ) {
        updateTypeCheckboxes( getOptions(), o );
        updateOptions( getOptions() );
        if ( getCurrentTreePanel() != null ) {
            final Options.PHYLOGENY_GRAPHICS_TYPE previous_type = getCurrentTreePanel().getPhylogenyGraphicsType();
            final Options.PHYLOGENY_GRAPHICS_TYPE new_type = getOptions().getPhylogenyGraphicsType();
            if ( ( ( previous_type == Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED ) && ( new_type != Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED ) )
                    || ( ( previous_type == Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR ) && ( new_type != Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR ) )
                    || ( ( previous_type != Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED ) && ( new_type == Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED ) )
                    || ( ( previous_type != Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR ) && ( new_type == Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR ) ) ) {
                getCurrentTreePanel().getControlPanel().showWhole();
            }
            if ( getCurrentTreePanel().isPhyHasBranchLengths() && ( new_type != Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR ) ) {
                getCurrentTreePanel().getControlPanel().setDrawPhylogramEnabled( true );
            }
            else {
                getCurrentTreePanel().getControlPanel().setDrawPhylogramEnabled( false );
            }
            getCurrentTreePanel().setPhylogenyGraphicsType( getOptions().getPhylogenyGraphicsType() );
            updateScreenTextAntialias( getMainPanel().getTreePanels() );
            if ( getCurrentTreePanel().getControlPanel().getDynamicallyHideData() != null ) {
                if ( new_type == Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED ) {
                    getCurrentTreePanel().getControlPanel().getDynamicallyHideData().setEnabled( false );
                }
                else {
                    getCurrentTreePanel().getControlPanel().getDynamicallyHideData().setEnabled( true );
                }
            }
        }
    }

    void updateOptions( final Options options ) {
        options.setAntialiasScreen( ( _screen_antialias_cbmi != null ) && _screen_antialias_cbmi.isSelected() );
        options.setBackgroundColorGradient( ( _background_gradient_cbmi != null )
                && _background_gradient_cbmi.isSelected() );
        options.setShowDomainLabels( ( _show_domain_labels != null ) && _show_domain_labels.isSelected() );
        options.setAbbreviateScientificTaxonNames( ( _abbreviate_scientific_names != null )
                && _abbreviate_scientific_names.isSelected() );
        options.setColorLabelsSameAsParentBranch( ( _color_labels_same_as_parent_branch != null )
                && _color_labels_same_as_parent_branch.isSelected() );
        options.setShowDefaultNodeShapes( ( _show_default_node_shapes_cbmi != null )
                && _show_default_node_shapes_cbmi.isSelected() );
        options.setTaxonomyColorizeNodeShapes( ( _taxonomy_colorize_node_shapes_cbmi != null )
                && _taxonomy_colorize_node_shapes_cbmi.isSelected() );
        if ( ( _non_lined_up_cladograms_rbmi != null ) && ( _non_lined_up_cladograms_rbmi.isSelected() ) ) {
            options.setCladogramType( Options.CLADOGRAM_TYPE.NON_LINED_UP );
        }
        else if ( ( _uniform_cladograms_rbmi != null ) && ( _uniform_cladograms_rbmi.isSelected() ) ) {
            options.setCladogramType( Options.CLADOGRAM_TYPE.TOTAL_NODE_SUM_DEP );
        }
        else if ( ( _ext_node_dependent_cladogram_rbmi != null ) && ( _ext_node_dependent_cladogram_rbmi.isSelected() ) ) {
            options.setCladogramType( Options.CLADOGRAM_TYPE.EXT_NODE_SUM_DEP );
        }
        options.setSearchCaseSensitive( ( _search_case_senstive_cbmi != null )
                && _search_case_senstive_cbmi.isSelected() );
        if ( ( _show_scale_cbmi != null ) && _show_scale_cbmi.isEnabled() ) {
            options.setShowScale( _show_scale_cbmi.isSelected() );
        }
        if ( _label_direction_cbmi != null ) {
            if ( _label_direction_cbmi.isSelected() ) {
                options.setNodeLabelDirection( Options.NODE_LABEL_DIRECTION.RADIAL );
            }
            else {
                options.setNodeLabelDirection( Options.NODE_LABEL_DIRECTION.HORIZONTAL );
            }
        }
        options.setShowOverview( ( _show_overview_cbmi != null ) && _show_overview_cbmi.isSelected() );
        options.setShowConfidenceStddev( ( _show_confidence_stddev_cbmi != null )
                && _show_confidence_stddev_cbmi.isSelected() );
        if ( ( _show_branch_length_values_cbmi != null ) && _show_branch_length_values_cbmi.isEnabled() ) {
            options.setShowBranchLengthValues( _show_branch_length_values_cbmi.isSelected() );
        }
        options.setPrintUsingActualSize( ( _print_using_actual_size_cbmi != null )
                && ( _print_using_actual_size_cbmi.isSelected() ) );
        options.setGraphicsExportUsingActualSize( ( _graphics_export_using_actual_size_cbmi != null )
                && ( _graphics_export_using_actual_size_cbmi.isSelected() ) );
        options.setAntialiasPrint( ( _antialias_print_cbmi != null ) && _antialias_print_cbmi.isSelected() );
        if ( ( _use_brackets_for_conf_in_nh_export_cbmi != null )
                && _use_brackets_for_conf_in_nh_export_cbmi.isSelected() ) {
            options.setNhConversionSupportValueStyle( PhylogenyNodeI.NH_CONVERSION_SUPPORT_VALUE_STYLE.IN_SQUARE_BRACKETS );
        }
        else if ( ( _use_internal_names_for_conf_in_nh_export_cbmi != null )
                && _use_internal_names_for_conf_in_nh_export_cbmi.isSelected() ) {
            options.setNhConversionSupportValueStyle( PhylogenyNodeI.NH_CONVERSION_SUPPORT_VALUE_STYLE.AS_INTERNAL_NODE_NAMES );
        }
        else {
            options.setNhConversionSupportValueStyle( PhylogenyNodeI.NH_CONVERSION_SUPPORT_VALUE_STYLE.NONE );
        }
        options.setPrintBlackAndWhite( ( _print_black_and_white_cbmi != null )
                && _print_black_and_white_cbmi.isSelected() );
        options.setInternalNumberAreConfidenceForNhParsing( ( _internal_number_are_confidence_for_nh_parsing_cbmi != null )
                && _internal_number_are_confidence_for_nh_parsing_cbmi.isSelected() );
        options.setExtractPfamTaxonomyCodesInNhParsing( ( _extract_pfam_style_tax_codes_cbmi != null )
                && _extract_pfam_style_tax_codes_cbmi.isSelected() );
        options.setReplaceUnderscoresInNhParsing( ( _replace_underscores_cbmi != null )
                && _replace_underscores_cbmi.isSelected() );
        options.setMatchWholeTermsOnly( ( _search_whole_words_only_cbmi != null )
                && _search_whole_words_only_cbmi.isSelected() );
        options.setInverseSearchResult( ( _inverse_search_result_cbmi != null )
                && _inverse_search_result_cbmi.isSelected() );
        if ( _graphics_export_visible_only_cbmi != null ) {
            options.setGraphicsExportVisibleOnly( _graphics_export_visible_only_cbmi.isSelected() );
            if ( _graphics_export_visible_only_cbmi.isSelected() && ( _graphics_export_using_actual_size_cbmi != null ) ) {
                _graphics_export_using_actual_size_cbmi.setSelected( true );
                _graphics_export_using_actual_size_cbmi.setEnabled( false );
            }
            else {
                _graphics_export_using_actual_size_cbmi.setEnabled( true );
            }
        }
        if ( ( _rectangular_type_cbmi != null ) && _rectangular_type_cbmi.isSelected() ) {
            options.setPhylogenyGraphicsType( Options.PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR );
        }
        else if ( ( _triangular_type_cbmi != null ) && _triangular_type_cbmi.isSelected() ) {
            options.setPhylogenyGraphicsType( Options.PHYLOGENY_GRAPHICS_TYPE.TRIANGULAR );
        }
        else if ( ( _curved_type_cbmi != null ) && _curved_type_cbmi.isSelected() ) {
            options.setPhylogenyGraphicsType( Options.PHYLOGENY_GRAPHICS_TYPE.CURVED );
        }
        else if ( ( _convex_type_cbmi != null ) && _convex_type_cbmi.isSelected() ) {
            options.setPhylogenyGraphicsType( Options.PHYLOGENY_GRAPHICS_TYPE.CONVEX );
        }
        else if ( ( _euro_type_cbmi != null ) && _euro_type_cbmi.isSelected() ) {
            options.setPhylogenyGraphicsType( Options.PHYLOGENY_GRAPHICS_TYPE.EURO_STYLE );
        }
        else if ( ( _rounded_type_cbmi != null ) && _rounded_type_cbmi.isSelected() ) {
            options.setPhylogenyGraphicsType( Options.PHYLOGENY_GRAPHICS_TYPE.ROUNDED );
        }
        else if ( ( _unrooted_type_cbmi != null ) && _unrooted_type_cbmi.isSelected() ) {
            options.setPhylogenyGraphicsType( Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED );
        }
        else if ( ( _circular_type_cbmi != null ) && _circular_type_cbmi.isSelected() ) {
            options.setPhylogenyGraphicsType( Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR );
        }
    }

    void updateTypeCheckboxes( final Options options, final Object o ) {
        setTypeMenuToAllUnselected();
        ( ( JCheckBoxMenuItem ) o ).setSelected( true );
    }

    void displayBasicInformation() {
        if ( ( _mainpanel.getCurrentPhylogeny() == null ) || _mainpanel.getCurrentPhylogeny().isEmpty() ) {
            return;
        }
        _textframe = TextFrame.instantiate( AptxUtil.createBasicInformation( _mainpanel.getCurrentPhylogeny() ) );
    }

    void viewAsNexus() {
        removeTextFrame();
        if ( ( _mainpanel.getCurrentPhylogeny() == null ) || _mainpanel.getCurrentPhylogeny().isEmpty()
                || ( _mainpanel.getCurrentPhylogeny().getNumberOfExternalNodes() > 20000 ) ) {
            return;
        }
        _textframe = TextFrame.instantiate( _mainpanel.getCurrentPhylogeny().toNexus( getOptions()
                .getNhConversionSupportValueStyle() ) );
    }

    void viewAsNH() {
        removeTextFrame();
        if ( ( _mainpanel.getCurrentPhylogeny() == null ) || _mainpanel.getCurrentPhylogeny().isEmpty()
                || ( _mainpanel.getCurrentPhylogeny().getNumberOfExternalNodes() > 20000 ) ) {
            return;
        }
        _textframe = TextFrame.instantiate( _mainpanel.getCurrentPhylogeny()
                .toNewHampshire( false, getOptions().getNhConversionSupportValueStyle() ) );
    }

    void viewAsNHX() {
        removeTextFrame();
        if ( ( _mainpanel.getCurrentPhylogeny() == null ) || _mainpanel.getCurrentPhylogeny().isEmpty()
                || ( _mainpanel.getCurrentPhylogeny().getNumberOfExternalNodes() > 10000 ) ) {
            return;
        }
        _textframe = TextFrame.instantiate( _mainpanel.getCurrentPhylogeny().toNewHampshireX() );
    }

    void viewAsXML() {
        removeTextFrame();
        if ( ( _mainpanel.getCurrentPhylogeny() != null ) && !_mainpanel.getCurrentPhylogeny().isEmpty()
                && ( _mainpanel.getCurrentPhylogeny().getNumberOfExternalNodes() <= 10000 ) ) {
            _textframe = TextFrame.instantiate( _mainpanel.getCurrentPhylogeny().toPhyloXML( 0 ) );
        }
    }

    /**
     * Display the about box.
     */
    static void about() {
        final StringBuffer about = new StringBuffer( "Archaeopteryx\nVersion " + Constants.VERSION + "\n" );
        about.append( "Copyright (C) 2007-2012 Christian M. Zmasek\n" );
        about.append( "All Rights Reserved\n" );
        about.append( "License: GNU Lesser General Public License (LGPL)\n" );
        about.append( "Last modified: " + Constants.PRG_DATE + "\n" );
        about.append( "Based on: " + ForesterUtil.getForesterLibraryInformation() + "\n" );
        about.append( "phyloXML version : " + ForesterConstants.PHYLO_XML_VERSION + "\n" );
        about.append( "phyloXML location: " + ForesterConstants.PHYLO_XML_LOCATION + "\n" );
        if ( !ForesterUtil.isEmpty( ForesterUtil.JAVA_VERSION ) && !ForesterUtil.isEmpty( ForesterUtil.JAVA_VENDOR ) ) {
            about.append( "[your Java version: " + ForesterUtil.JAVA_VERSION + " " + ForesterUtil.JAVA_VENDOR + "]\n" );
        }
        if ( !ForesterUtil.isEmpty( ForesterUtil.OS_NAME ) && !ForesterUtil.isEmpty( ForesterUtil.OS_ARCH )
                && !ForesterUtil.isEmpty( ForesterUtil.OS_VERSION ) ) {
            about.append( "[your OS: " + ForesterUtil.OS_NAME + " " + ForesterUtil.OS_ARCH + " "
                    + ForesterUtil.OS_VERSION + "]\n" );
        }
        final Runtime rt = java.lang.Runtime.getRuntime();
        final long free_memory = rt.freeMemory() / 1000000;
        final long total_memory = rt.totalMemory() / 1000000;
        about.append( "[free memory: " + free_memory + "MB, total memory: " + total_memory + "MB]\n" );
        about.append( "[locale: " + Locale.getDefault() + "]\n" );
        about.append( "References:\n" );
        about.append( Constants.PHYLOXML_REFERENCE_SHORT + "\n" );
        about.append( "For more information & download:\n" );
        about.append( Constants.APTX_WEB_SITE + "\n" );
        about.append( "Comments: " + Constants.AUTHOR_EMAIL );
        JOptionPane.showMessageDialog( null, about, Constants.PRG_NAME, JOptionPane.PLAIN_MESSAGE );
    }

    static String createCurrentFontDesc( final TreeFontSet tree_font_set ) {
        return tree_font_set.getLargeFont().getFamily() + " " + tree_font_set.getLargeFont().getSize();
    }

    static JMenu createMenu( final String title, final Configuration conf ) {
        final JMenu jmenu = new JMenu( title );
        if ( !conf.isUseNativeUI() ) {
            jmenu.setFont( org.forester.archaeopteryx.MainFrame.menu_font );
            jmenu.setBackground( conf.getGuiMenuBackgroundColor() );
            jmenu.setForeground( conf.getGuiMenuTextColor() );
        }
        return jmenu;
    }

    static JMenuItem customizeMenuItemAsLabel( final JMenuItem label, final Configuration configuration ) {
        label.setFont( org.forester.archaeopteryx.MainFrame.menu_font.deriveFont( Font.BOLD ) );
        if ( !configuration.isUseNativeUI() ) {
            label.setBackground( configuration.getGuiMenuBackgroundColor() );
            label.setForeground( configuration.getGuiMenuTextColor() );
            label.setOpaque( true );
        }
        label.setSelected( false );
        label.setEnabled( false );
        return label;
    }

    static void cycleOverview( final Options op, final TreePanel tree_panel ) {
        switch ( op.getOvPlacement() ) {
            case LOWER_LEFT:
                op.setOvPlacement( Options.OVERVIEW_PLACEMENT_TYPE.UPPER_LEFT );
                break;
            case LOWER_RIGHT:
                op.setOvPlacement( Options.OVERVIEW_PLACEMENT_TYPE.LOWER_LEFT );
                break;
            case UPPER_LEFT:
                op.setOvPlacement( Options.OVERVIEW_PLACEMENT_TYPE.UPPER_RIGHT );
                break;
            case UPPER_RIGHT:
                op.setOvPlacement( Options.OVERVIEW_PLACEMENT_TYPE.LOWER_RIGHT );
                break;
            default:
                throw new RuntimeException( "unknown placement: " + op.getOvPlacement() );
        }
        if ( tree_panel != null ) {
            tree_panel.updateOvSettings();
        }
    }

    static void cycleNodeFill( final Options op, final TreePanel tree_panel ) {
        switch ( op.getDefaultNodeFill() ) {
            case GRADIENT:
                op.setDefaultNodeFill( NodeVisualization.NodeFill.SOLID );
                break;
            case NONE:
                op.setDefaultNodeFill( NodeVisualization.NodeFill.GRADIENT );
                break;
            case SOLID:
                op.setDefaultNodeFill( NodeVisualization.NodeFill.NONE );
                break;
            default:
                throw new RuntimeException( "unknown fill: " + op.getDefaultNodeFill() );
        }
    }

    static void cycleNodeShape( final Options op, final TreePanel tree_panel ) {
        switch ( op.getDefaultNodeShape() ) {
            case CIRCLE:
                op.setDefaultNodeShape( NodeVisualization.NodeShape.RECTANGLE );
                break;
            case RECTANGLE:
                op.setDefaultNodeShape( NodeVisualization.NodeShape.CIRCLE );
                break;
            default:
                throw new RuntimeException( "unknown shape: " + op.getDefaultNodeShape() );
        }
    }

    static void help( final Map<String, WebLink> weblinks ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Display options\n" );
        sb.append( "-------------------\n" );
        sb.append( "Use the checkboxes to select types of information to display on the tree.\n\n" );
        sb.append( "Clickable tree nodes\n" );
        sb.append( "--------------------\n" );
        sb.append( "Tree nodes can be clicked, the action is determined by the 'click on node to' menu\n" );
        sb.append( "or by right clicking:\n" );
        sb.append( "o  Display Node Data -- display information for a node\n" );
        sb.append( "o  Collapse/Uncollapse -- collapse and uncollapse subtree from clicked node\n" );
        sb.append( "o  Root/Reroot -- change tree root to clicked node\n" );
        sb.append( "o  Sub/Super Tree -- toggle between subtree from clicked node and whole tree\n" );
        sb.append( "o  Swap Descendants -- switch descendant on either side of clicked node\n" );
        sb.append( "o  Colorize Subtree -- color a subtree\n" );
        sb.append( "o  Open Sequence Web -- launch a web browser to display sequence information\n" );
        sb.append( "o  Open Taxonomy Web -- launch a web browser to display taxonomy information\n" );
        sb.append( "-  there may be additional choices depending on this particular setup\n\n" );
        sb.append( "Right clicking on a node always displays the information of a node.\n\n" );
        sb.append( "Zooming\n" );
        sb.append( "---------\n" );
        sb.append( "The mouse wheel and the plus and minus keys control zooming.\n" );
        sb.append( "Mouse wheel+Ctrl changes the text size.\n" );
        sb.append( "Mouse wheel+Shift controls zooming in vertical direction only.\n" );
        sb.append( "Use the buttons on the control panel to zoom the tree in and out, horizontally or vertically.\n" );
        sb.append( "The entire tree can be fitted into the window by clicking the \"F\" button, or by pressing F, Delete, or Home.\n" );
        sb.append( "The up, down, left, and right keys can be used to move the visible part (if zoomed in).\n" );
        sb.append( "Up, down, left, and right+Shift can be used to control zooming horizontally and vertically.\n" );
        sb.append( "Plus and minus keys+Ctrl change the text size; F+Ctrl, Delete+Ctrl, or Home+Ctrl resets it.\n\n" );
        sb.append( "Quick tree manipulation:\n" );
        sb.append( "------------------------\n" );
        sb.append( "Order Subtrees -- order the tree by branch length\n" );
        sb.append( "Uncollapse All -- uncollapse any and all collapsed branches\n\n" );
        sb.append( "Memory problems (Java heap space error)\n" );
        sb.append( "---------------------------------------\n" );
        sb.append( "Since the Java default memory allocation is quite small, it might by necessary (for trees\n" );
        sb.append( "with more than approximately 5000 external nodes) to increase the memory which Java can use, with\n" );
        sb.append( "the '-Xmx' Java command line option. For example:\n" );
        sb.append( "java -Xms32m -Xmx256m -cp path\\to\\forester.jar org.forester.archaeopteryx.Archaeopteryx\n\n" );
        if ( ( weblinks != null ) && ( weblinks.size() > 0 ) ) {
            sb.append( "Active web links\n" );
            sb.append( "--------------------\n" );
            for( final String key : weblinks.keySet() ) {
                sb.append( " " + weblinks.get( key ).toString() + "\n" );
            }
        }
        // + "General remarks\n"
        // + "---------------\n"
        // +
        // "o  The application version permits copying to the clipboard \n"
        // +
        // "    in the \"View\"|\"View as ...\" frame (either by control-c or button press).\n"
        // +
        // "o  Changes made to a subtree affect this subtree and its subtrees,\n"
        // + "    but not any of its parent tree(s).\n"
        // +
        // "o  Archaeopteryx tries to detect whether the numerical values in a NH tree\n"
        // +
        // "    are likely to be bootstrap values instead of branch length values.\n\n"
        // +
        // " Remarks regarding SDI (Speciation Duplication Inference):\n"
        // +
        // "o  Each external node of the gene tree (in display) needs to be associated with\n"
        // +
        // "    a species: either directly through the \"Species\" field, or the species\n"
        // +
        // "    is part of the sequence name in the form \"XXXX_SPECIES\"\n"
        // +
        // "    (e.g. \"ACON_DROME\" or \"ACON_DROME/123-4489\" which is also acceptable).\n"
        // +
        // "o  A species tree for each species of the gene tree needs to be loaded with\n"
        // +
        // "   \"SDI\"|\"Load species tree\" prior the SDI execution.\n"
        // +
        // "o  !External nodes of the gene tree associated with species not present in\n"
        // +
        // "    the species tree are REMOVED prior to SDI execution!\n"
        // +
        // "o  Both the gene tree and the species tree must be completely binary.\n"
        // +
        // "o  Duplications and speciations are a function of the position of the root.\n"
        // +
        // "    Hence, after each manual \"Root/Reroot\"ing some duplications will be\n"
        // + "    incorrect and need to be inferred again\n"
        // +
        // "    with: \"SDI\"|\"SDI (Speciation Duplication Inference)\n\n"
        sb.append( "\n" );
        sb.append( "phyloXML\n" );
        sb.append( "-------------------\n" );
        sb.append( "Reference: " + Constants.PHYLOXML_REFERENCE + "\n" );
        sb.append( "Website: " + Constants.PHYLOXML_WEB_SITE + "\n" );
        sb.append( "Version: " + ForesterConstants.PHYLO_XML_VERSION + "\n" );
        sb.append( "\n" );
        sb.append( "For more information: http://www.phylosoft.org/archaeopteryx/\n" );
        sb.append( "Email: " + Constants.AUTHOR_EMAIL + "\n\n" );
        TextFrame.instantiate( sb.toString() );
    }

    static void setOvPlacementColorChooseMenuItem( final JMenuItem mi, final Options options ) {
        if ( ( options != null ) && ( options.getOvPlacement() != null ) ) {
            mi.setText( "Cycle Overview Placement... (current: " + options.getOvPlacement() + ")" );
        }
        else {
            mi.setText( "Cycle Overview Placement..." );
        }
    }

    static void setCycleNodeFillMenuItem( final JMenuItem mi, final Options options ) {
        if ( ( options != null ) && ( options.getDefaultNodeFill() != null ) ) {
            mi.setText( "Cycle Node Shape Fill Type... (current: "
                    + options.getDefaultNodeFill().toString().toLowerCase() + ")" );
        }
        else {
            mi.setText( "Cycle Node Shape Fill Type..." );
        }
    }

    static void setCycleNodeShapeMenuItem( final JMenuItem mi, final Options options ) {
        if ( ( options != null ) && ( options.getDefaultNodeShape() != null ) ) {
            mi.setText( "Cycle Node Shape Fill Type... (current: "
                    + options.getDefaultNodeShape().toString().toLowerCase() + ")" );
        }
        else {
            mi.setText( "Cycle Node Shape Fill Type..." );
        }
    }

    static void setTextColorChooseMenuItem( final JMenuItem mi, final TreePanel tree_panel ) {
        if ( ( tree_panel != null ) && ( tree_panel.getTreeColorSet() != null ) ) {
            mi.setText( "Select Colors... (current: " + tree_panel.getTreeColorSet().getCurrentColorSchemeName() + ")" );
        }
        else {
            mi.setText( "Select Colors..." );
        }
    }

    static void setTextForFontChooserMenuItem( final JMenuItem mi, final String font_desc ) {
        mi.setText( "Select Font... (current: " + font_desc + ")" );
    }

    static void setTextMinSupportMenuItem( final JMenuItem mi, final Options options, final TreePanel current_tree_panel ) {
        if ( ( current_tree_panel == null ) || ( current_tree_panel.getPhylogeny() == null ) ) {
            mi.setEnabled( true );
        }
        else if ( AptxUtil.isHasAtLeastOneBranchWithSupportValues( current_tree_panel.getPhylogeny() ) ) {
            mi.setEnabled( true );
        }
        else {
            mi.setEnabled( false );
        }
        mi.setText( "Enter Min Confidence Value... (current: " + options.getMinConfidenceValue() + ")" );
    }

    static void setTextNodeSizeMenuItem( final JMenuItem mi, final Options options ) {
        mi.setText( "Enter Default Node Shape Size... (current: " + options.getDefaultNodeShapeSize() + ")" );
    }

    static void updateOptionsMenuDependingOnPhylogenyType( final MainPanel main_panel,
                                                           final JCheckBoxMenuItem scale,
                                                           final JCheckBoxMenuItem branch_lengths,
                                                           final JRadioButtonMenuItem non_lined_up,
                                                           final JRadioButtonMenuItem uniform_clado,
                                                           final JRadioButtonMenuItem nonuniform_clado,
                                                           final JCheckBoxMenuItem label_direction_cbmi ) {
        final TreePanel tree_panel = main_panel.getCurrentTreePanel();
        final ControlPanel control = main_panel.getControlPanel();
        final Options options = main_panel.getOptions();
        scale.setSelected( options.isShowScale() );
        branch_lengths.setSelected( options.isShowBranchLengthValues() );
        // non_lined_up.setSelected( options.isNonLinedUpCladogram() );
        if ( ( tree_panel != null ) && ( !tree_panel.isPhyHasBranchLengths() ) ) {
            scale.setSelected( false );
            scale.setEnabled( false );
            branch_lengths.setSelected( false );
            branch_lengths.setEnabled( false );
        }
        else if ( ( tree_panel != null ) && !control.isDrawPhylogram() ) {
            scale.setSelected( false );
            scale.setEnabled( false );
            branch_lengths.setEnabled( true );
        }
        else {
            scale.setEnabled( true );
            branch_lengths.setEnabled( true );
        }
        if ( ( tree_panel != null )
                && ( ( tree_panel.getPhylogenyGraphicsType() != Options.PHYLOGENY_GRAPHICS_TYPE.ROUNDED )
                        && ( tree_panel.getPhylogenyGraphicsType() != Options.PHYLOGENY_GRAPHICS_TYPE.EURO_STYLE ) && ( tree_panel
                        .getPhylogenyGraphicsType() != Options.PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR ) ) ) {
            branch_lengths.setSelected( false );
            branch_lengths.setEnabled( false );
        }
        if ( tree_panel != null ) {
            if ( ( tree_panel.getPhylogenyGraphicsType() == Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR )
                    || ( tree_panel.getPhylogenyGraphicsType() == Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED ) ) {
                non_lined_up.setEnabled( false );
                uniform_clado.setEnabled( false );
                nonuniform_clado.setEnabled( false );
            }
            else {
                non_lined_up.setEnabled( true );
                uniform_clado.setEnabled( true );
                nonuniform_clado.setEnabled( true );
            }
        }
        else {
            if ( ( tree_panel != null )
                    && ( ( tree_panel.getPhylogenyGraphicsType() != Options.PHYLOGENY_GRAPHICS_TYPE.EURO_STYLE ) && ( tree_panel
                            .getPhylogenyGraphicsType() != Options.PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR ) ) ) {
                branch_lengths.setSelected( false );
                branch_lengths.setEnabled( false );
            }
            if ( ( tree_panel != null )
                    && ( ( tree_panel.getPhylogenyGraphicsType() == Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR ) || ( tree_panel
                            .getPhylogenyGraphicsType() == Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED ) ) ) {
                non_lined_up.setEnabled( false );
            }
            else {
                // non_lined_up.setSelected( options.isNonLinedUpCladogram() );
                non_lined_up.setEnabled( true );
            }
        }
        label_direction_cbmi.setEnabled( true );
        if ( tree_panel != null ) {
            if ( ( tree_panel.getPhylogenyGraphicsType() != Options.PHYLOGENY_GRAPHICS_TYPE.UNROOTED )
                    && ( tree_panel.getPhylogenyGraphicsType() != Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR ) ) {
                label_direction_cbmi.setEnabled( false );
            }
            if ( tree_panel.getPhylogenyGraphicsType() == Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR ) {
                scale.setSelected( false );
                scale.setEnabled( false );
            }
        }
    }

    static void updateScreenTextAntialias( final List<TreePanel> treepanels ) {
        for( final TreePanel tree_panel : treepanels ) {
            tree_panel.setTextAntialias();
        }
    }
}

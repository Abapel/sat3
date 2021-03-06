/*
 * Copyright (c) 2010 AnjLab
 * 
 * This file is part of 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem 
 * is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem
 * is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.anjlab.sat3;

import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._001_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._010_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._011_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._100_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._101_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._110_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._111_instance;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;

public class TestHelper
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        Helper.UseUniversalVarNames = true;
        System.out.println(TestHelper.class.getName());
    }
    
    @Test
    public void testCreateRandomFormulaWithRespectToMaxM()
    {
        ITabularFormula formula = Helper.createRandomFormula(new Random(), 3, 6);
        
        assertEquals(3, formula.getVarCount());
        //  Clauses count not really plays any role in this implementation
//        assertEquals(6, formula.getClausesCount());
        
        try
        {
            formula = Helper.createRandomFormula(new Random(), 4, 700);
                        
            Assert.fail("This implementation assumes duplicated triplets are not allowed");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("3-SAT formula of 4 variables may have at most 64 valuable clauses, but requested to create formula with 700 clauses", e.getMessage());
        }
    }
    
    @Test
    public void testUnify() throws Exception
    {
        ICompactTripletsStructure s1 = (ICompactTripletsStructure) Helper.createFormula(
                new int[] 
                        {                //        a b c d e f g h
                            1, 2, -3,    //        0 0 1          
                            -1, 2, -3,   //        1 0 1          
                            -1, -2, 3,   //        1 1 0          
                            2, -3, 4,    //          0 1 0        
                            2, -3, -4,   //          0 1 1        
                            -2, 3, 4,    //          1 0 0        
                            -2, 3, -4,   //          1 0 1        
                            3, 4, -5,    //            0 0 1      
                            3, -4, 5,    //            0 1 0      
                            3, -4, -5,   //            0 1 1      
                            -3, 4, -5,   //            1 0 1      
                            -3, -4, -5,  //            1 1 1      
                            4, -5, -6,   //              0 1 1    
                            -4, 5, 6,    //              1 0 0    
                            -4, -5, 6,   //              1 1 0    
                            -4, -5, -6,  //              1 1 1    
                            5, 6, -7,    //                0 0 1  
                            -5, 6, -7,   //                1 0 1  
                            -5, -6, 7,   //                1 1 0  
                            6, -7, -8,   //                  0 1 1
                            -6, 7, 8     //                  1 0 0
                        });              //       VarCount: 8; ClausesCount: 21; TiersCount: 6
        
        
        assertEquals(21, s1.getClausesCount());
        
        Helper.prettyPrint(s1);
        
        ICompactTripletsStructure s2 = (ICompactTripletsStructure) Helper.createFormula(
                new int[] 
                        {                //        h g b e a f c d
                            8, 7, 2,     //        0 0 0          
                            8, -7, -2,   //        0 1 1          
                            -8, -7, 2,   //        1 1 0          
                            7, 2, -5,    //          0 0 1        
                            -7, 2, -5,   //          1 0 1        
                            -7, -2, 5,   //          1 1 0        
                            2, -5, 1,    //            0 1 0      
                            2, -5, -1,   //            0 1 1      
                            -2, 5, -1,   //            1 0 1      
                            -5, 1, 6,    //              0 1 0    
                            -5, 1, -6,   //              1 0 0    
                            5, -1, 6,    //              1 0 1    
                            -5, -1, -6,  //              1 1 1    
                            1, 6, -3,    //                0 0 1  
                            1, -6, -3,   //                0 1 1  
                            -1, 6, 3,    //                1 0 0  
                            -1, -6, -3,  //                1 1 1  
                            6, -3, -4,   //                  0 0 1
                            6, 3, -4,    //                  0 1 1
                            -6, -3, 4,   //                  1 1 0
                            -6, -3, -4   //                  1 1 1
                        });              //       VarCount: 8; ClausesCount: 21; TiersCount: 6
        
        assertEquals(21, s2.getClausesCount());
        
        Helper.prettyPrint(s2);
        
        //  List of ITabularFormula
        ObjectArrayList cts = new ObjectArrayList(new ITabularFormula[] {s1, s2});
        
        Helper.unify(cts);
        
        assertEquals(13, s1.getClausesCount());
        assertEquals(15, s2.getClausesCount());
        
        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);
        
        assertTrue(s1.getTier(0).contains(_001_instance));
        assertTrue(s1.getTier(0).contains(_101_instance));
        assertTrue(s1.getTier(1).contains(_010_instance));
        assertTrue(s1.getTier(1).contains(_011_instance));
        assertTrue(s1.getTier(2).contains(_101_instance));
        assertTrue(s1.getTier(2).contains(_111_instance));
        assertTrue(s1.getTier(3).contains(_011_instance));
        assertTrue(s1.getTier(3).contains(_110_instance));
        assertTrue(s1.getTier(3).contains(_111_instance));
        assertTrue(s1.getTier(4).contains(_101_instance));
        assertTrue(s1.getTier(4).contains(_110_instance));
        assertTrue(s1.getTier(5).contains(_011_instance));
        assertTrue(s1.getTier(5).contains(_100_instance));
        
        assertTrue(s2.getTier(0).contains(_000_instance));
        assertTrue(s2.getTier(0).contains(_110_instance));
        assertTrue(s2.getTier(1).contains(_001_instance));
        assertTrue(s2.getTier(1).contains(_101_instance));
        assertTrue(s2.getTier(2).contains(_010_instance));
        assertTrue(s2.getTier(2).contains(_011_instance));
        assertTrue(s2.getTier(3).contains(_100_instance));
        assertTrue(s2.getTier(3).contains(_101_instance));
        assertTrue(s2.getTier(3).contains(_111_instance));
        assertTrue(s2.getTier(4).contains(_001_instance));
        assertTrue(s2.getTier(4).contains(_011_instance));
        assertTrue(s2.getTier(4).contains(_111_instance));
        assertTrue(s2.getTier(5).contains(_011_instance));
        assertTrue(s2.getTier(5).contains(_110_instance));
        assertTrue(s2.getTier(5).contains(_111_instance));
    }
    
    @Test
    public void testGetCanonicalVarName3()
    {
        assertEquals(3, Helper.getCanonicalVarName3(1, 2, new int[] {1, 2, 3}));
        assertEquals(3, Helper.getCanonicalVarName3(2, 1, new int[] {1, 2, 3}));
        assertEquals(1, Helper.getCanonicalVarName3(2, 3, new int[] {1, 2, 3}));
        assertEquals(1, Helper.getCanonicalVarName3(3, 2, new int[] {1, 2, 3}));
        assertEquals(2, Helper.getCanonicalVarName3(1, 3, new int[] {1, 2, 3}));
        assertEquals(2, Helper.getCanonicalVarName3(3, 1, new int[] {1, 2, 3}));
    }

    @Test
    public void testCreateCTF()
    {
        for (int n = 7; n < 100; n++)
        {
            try
            {
                System.out.println(n);
                ITabularFormula formula = Helper.createRandomFormula(21, n);
                ObjectArrayList ctf = Helper.createCTF(formula);
                Helper.completeToCTS(ctf, formula.getPermutation());
            }
            catch (EmptyStructureException e)
            {
            }
        }
    }
    
    @Test
    public void testCreateCTS() throws IOException
    {
        ITabularFormula formula = Helper.loadFromFile("target/test-classes/simplesat-example.cnf");
        Helper.prettyPrint(formula);
        ObjectArrayList ctf = Helper.createCTF(formula);
        Helper.printFormulas(ctf);
        Helper.completeToCTS(ctf, formula.getPermutation());
        Helper.printFormulas(ctf);
        
        ICompactTripletsStructure s = (ICompactTripletsStructure)ctf.get(0);
        
        assertEquals(5, s.getVarCount());
        assertEquals(21, s.getClausesCount());
    }
    
    @Test
    public void testConvertCTStructuresToRomanovSKTFileFormat() throws Exception
    {
        String filename = "target/test-classes/cnf-v22-c73-12.cnf";
        File file = new File(filename);
        ITabularFormula formula = Helper.loadFromFile(filename);
        ObjectArrayList ctf = Helper.createCTF(formula);
        System.out.println("CTF: " + ctf.size());

        Helper.completeToCTS(ctf, formula.getPermutation());
        
        System.out.println("CTS: " + ctf.size());
        
        Helper.convertCTStructuresToRomanovSKTFileFormat(ctf, "target/" + file.getName() + ".skt");
        Helper.saveToDIMACSFileFormat((ITabularFormula) ctf.get(0), "target/" + file.getName() + "-cts-0.cnf");
    }
    
    @Test
    public void testSaveLoadHSS() throws Exception
    {
        String filename = "target/test-classes/article-example.cnf";
        ITabularFormula formula = Helper.loadFromFile(filename);
        ObjectArrayList ct = Helper.createCTF(formula);
        Helper.completeToCTS(ct, formula.getPermutation());
        Helper.unify(ct);
        Properties statistics = new Properties();
        ObjectArrayList hss = Helper.createHyperStructuresSystem(ct, statistics);
        String hssPath = filename + "-hss";
        Helper.saveHSS(hssPath, hss);
        ObjectArrayList hss2 = Helper.loadHSS(hssPath);
        
        assertEquals(hss.size(), hss2.size());
        
        for (int h = 0; h < hss.size(); h++)
        {
            IHyperStructure hs = (IHyperStructure) hss.get(h);
            IHyperStructure hs2 = (IHyperStructure) hss2.get(h);
            
            assertEquals(hs.getTiers().size(), hs2.getTiers().size());
            
            for (int j = 0; j < hs.getTiers().size(); j++)
            {
                OpenIntObjectHashMap tier = (OpenIntObjectHashMap) hs.getTiers().get(j);
                OpenIntObjectHashMap tier2 = (OpenIntObjectHashMap) hs2.getTiers().get(j);
                
                assertEquals(tier.size(), tier2.size());
                
                for (int i = 0; i < tier.size(); i++)
                {
                    int key = tier.keys().get(i);
                    
                    assertTrue(tier2.containsKey(key));
                    
                    IVertex vertex = (IVertex) tier.get(key);
                    IVertex vertex2 = (IVertex) tier2.get(key);
                    
                    assertVerticesEqual(vertex, vertex2);
                    assertVerticesEqual(vertex.getBottomVertex1(), vertex2.getBottomVertex1());
                    assertVerticesEqual(vertex.getBottomVertex2(), vertex2.getBottomVertex2());
                }
            }
        }
    }

    private void assertVerticesEqual(IVertex vertex, IVertex vertex2)
    {
        if (vertex == null && vertex2 == null)
        {
            return;
        }
        assertArrayEquals(vertex.getPermutation().getABC(), vertex2.getPermutation().getABC());
        assertEquals(vertex.getTierIndex(), vertex2.getTierIndex());
        assertEquals(vertex.getTripletValue(), vertex2.getTripletValue());
        assertEquals(vertex.isBottom1Empty(), vertex2.isBottom1Empty());
        assertEquals(vertex.isBottom2Empty(), vertex2.isBottom2Empty());
        assertEquals(vertex.getCTS(), vertex2.getCTS());
    }
}
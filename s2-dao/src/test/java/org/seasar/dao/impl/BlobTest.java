/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Random;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author manhole
 */
public class BlobTest extends S2TestCase {

    private LargeBinaryDao largeBinaryByteArrayDao;

    private LargeBinaryStreamDao largeBinaryStreamDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("BlobTest.dicon");
    }

    /*
     * レコードが無い場合はnullを返すこと。
     */
    public void testBinary1Tx() throws Exception {
        assertNotNull(largeBinaryByteArrayDao);
        final LargeBinary largeBinary = largeBinaryByteArrayDao
                .getLargeBinary(123);
        assertEquals(null, largeBinary);
    }

    /*
     * INSERT -> SELECT -> UPDATE -> DELETEできること。
     */
    public void testBinary2Tx() throws Exception {
        {
            final LargeBinary largeBinary = new LargeBinary();
            largeBinary.setId(111);
            largeBinary.setLargeBinary("aabbcc".getBytes());
            largeBinaryByteArrayDao.insert(largeBinary);
        }
        {
            final LargeBinary largeBinary = largeBinaryByteArrayDao
                    .getLargeBinary(111);
            assertEquals("aabbcc".getBytes(), largeBinary.getLargeBinary());
            assertEquals(0, largeBinary.getVersionNo());

            largeBinary.setLargeBinary("ABCDEFG".getBytes());
            largeBinaryByteArrayDao.update(largeBinary);
        }
        {
            final LargeBinary largeBinary = largeBinaryByteArrayDao
                    .getLargeBinary(111);
            assertEquals("ABCDEFG".getBytes(), largeBinary.getLargeBinary());
            assertEquals(1, largeBinary.getVersionNo());

            largeBinaryByteArrayDao.delete(largeBinary);
        }
        {
            final LargeBinary largeBinary = largeBinaryByteArrayDao
                    .getLargeBinary(111);
            assertEquals(null, largeBinary);
        }
    }

    /*
     * 5MBのデータを入出力できること。
     * 
     * [Seasar-user:4834]のスレッドで、2.3KBあるデータが86byteになってしまうとの
     * 現象があったためこのテストで確認。
     * derbyでは問題ないようです。
     */
    public void testBinary3Tx() throws Exception {
        final Random random = new Random();
        final byte[] bytes = new byte[1024 * 5];
        random.nextBytes(bytes);
        {
            LargeBinary largeBinary = new LargeBinary();
            largeBinary.setId(4321);
            largeBinary.setLargeBinary(bytes);
            largeBinaryByteArrayDao.insert(largeBinary);
        }
        {
            final LargeBinary largeBinary = largeBinaryByteArrayDao
                    .getLargeBinary(4321);
            assertEquals(bytes, largeBinary.getLargeBinary());
            assertEquals(0, largeBinary.getVersionNo());
        }
    }

    public void testBinaryStream1Tx() throws Exception {
        assertNotNull(largeBinaryStreamDao);
        final LargeBinaryStream largeBinary = largeBinaryStreamDao
                .getLargeBinary(123);
        assertEquals(null, largeBinary);
    }

    public void no_testBinaryStream2Tx() throws Exception {
        {
            LargeBinaryStream largeBinary = new LargeBinaryStream();
            largeBinary.setId(321);
            largeBinary.setLargeBinary(toInputStream("zxcvb"));
            largeBinaryStreamDao.insert(largeBinary);
        }
        {
            final LargeBinaryStream largeBinary = largeBinaryStreamDao
                    .getLargeBinary(321);
            assertEquals(toInputStream("zxcvb"), largeBinary.getLargeBinary());
            assertEquals(0, largeBinary.getVersionNo());

            largeBinary.setLargeBinary(toInputStream("AAA"));
            largeBinaryStreamDao.update(largeBinary);
        }
        {
            final LargeBinaryStream largeBinary = largeBinaryStreamDao
                    .getLargeBinary(321);
            assertEquals(toInputStream("AAA"), largeBinary.getLargeBinary());
            assertEquals(1, largeBinary.getVersionNo());
        }
    }

    private InputStream toInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }

    private void assertEquals(InputStream expected, InputStream actual)
            throws IOException {
        while (true) {
            int exp = expected.read();
            int act = actual.read();
            if (exp != act) {
                fail();
                return;
            }
            if (exp == -1 || act == -1) {
                return;
            }
        }
    }

    private void assertEquals(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            fail();
            return;
        }
        for (int i = 0; i < actual.length; i++) {
            if (expected[i] != actual[i]) {
                fail();
                return;
            }
        }
    }

    public static interface LargeBinaryDao {

        public Class BEAN = LargeBinary.class;

        public String getLargeBinary_ARGS = "id";

        public LargeBinary getLargeBinary(int id);

        public void insert(LargeBinary largeBinary);

        public void update(LargeBinary largeBinary);

        public void delete(LargeBinary largeBinary);

    }

    public static interface LargeBinaryStreamDao {

        public Class BEAN = LargeBinaryStream.class;

        public String getLargeBinary_ARGS = "id";

        public LargeBinaryStream getLargeBinary(int id);

        public void insert(LargeBinaryStream largeBinary);

        public void update(LargeBinaryStream largeBinary);

    }

    public static class LargeBinary implements Serializable {

        private static final long serialVersionUID = 1L;

        public static final String TABLE = "LARGE_BINARY";

        private int id;

        private byte[] largeBinary;

        private int versionNo;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public byte[] getLargeBinary() {
            return largeBinary;
        }

        public void setLargeBinary(byte[] largeBinary) {
            this.largeBinary = largeBinary;
        }

        public int getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(int versionNo) {
            this.versionNo = versionNo;
        }
    }

    public static class LargeBinaryStream implements Serializable {

        private static final long serialVersionUID = 1L;

        public static final String TABLE = "LARGE_BINARY";

        private int id;

        private InputStream largeBinary;

        private int versionNo;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public InputStream getLargeBinary() {
            return largeBinary;
        }

        public void setLargeBinary(InputStream largeBinary) {
            this.largeBinary = largeBinary;
        }

        public int getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(int versionNo) {
            this.versionNo = versionNo;
        }
    }

}

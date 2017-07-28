package com.TyrLib2.PC.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Logger extends FilterOutputStream {
	
	public static void init(String fileName) {
		try {
			System.setOut(new PrintStream(new Logger(new FileOutputStream(fileName), System.out )));
			System.setErr(System.out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private FilterOutputStream branch;
	
	public Logger(final OutputStream out, final OutputStream branch) {
		super(out);
		this.branch = new FilterOutputStream(branch);
	}
	
    @Override
    public synchronized void write(final byte[] b, final int off, final int len) throws IOException {
        super.write(b, off, len);
    }

    /**
     * Write a byte to both streams.
     * @param b the byte to write
     * @throws IOException if an I/O error occurs
     */
    @Override
    public synchronized void write(final int b) throws IOException {
        super.write(b);
        this.branch.write(b);
    }

    /**
     * Flushes both streams.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void flush() throws IOException {
        super.flush();
        this.branch.flush();
    }

    /**
     * Closes both output streams.
     * 
     * If closing the main output stream throws an exception, attempt to close the branch output stream.
     * 
     * If closing the main and branch output streams both throw exceptions, which exceptions is thrown by this method is
     * currently unspecified and subject to change.
     * 
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            this.branch.close();
        }
    }
}

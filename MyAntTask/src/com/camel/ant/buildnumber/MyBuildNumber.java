package com.camel.ant.buildnumber;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MyBuildNumber extends Task {
	private static final BuildNumber mBuildNumber = new BuildNumber();
	
    // The method executing the task
    public void execute() throws BuildException {
    	boolean bIOflag = false;
    	int iBuildNumberNetwork = -1;
    	int iBuildNumberLocal = -1;
    	int iBuildNumber = -1;
		try {
			Freesia fsia = new Freesia();
			String sJson = fsia.getBuildNumber();
			iBuildNumberNetwork = getBdnum(sJson);
			
			mBuildNumber.setTask(this);
			iBuildNumberLocal = mBuildNumber.getBuildNumber();
			System.out.println("iBuildNumberNetwork = " + iBuildNumberNetwork);
			System.out.println("iBuildNumberLocal = " + iBuildNumberLocal);
			
			if (iBuildNumberNetwork >= iBuildNumberLocal) {
				mBuildNumber.updateBuildNumber(iBuildNumberNetwork+1);
				iBuildNumber = iBuildNumberNetwork;
			}
			else {
				mBuildNumber.updateBuildNumber(iBuildNumberLocal + 1);
				fsia.updateBuildNumber(iBuildNumberLocal + 1);
				iBuildNumber = iBuildNumberLocal;
			}
			//Finally set the property
	        getProject().setNewProperty("build.number", String.valueOf(iBuildNumber));
	        return;
		} catch (BuildException e) {
			e.printStackTrace();
			throw e;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			bIOflag = true;
		} catch (IOException e) {
			e.printStackTrace();
			bIOflag = true;
		} catch (Exception e) {
			e.printStackTrace();
			bIOflag = true;
		}
		
		if (bIOflag) {
			System.out.println("Since network error, we use the local build.number......");
			mBuildNumber.setTask(this);
			mBuildNumber.execute();
		}
    }
    
    private int getBdnum(String sJson) {
    	JSONObject jo = (JSONObject) JSON.parse(sJson);
    	String sData = jo.getString("data");
    	System.out.println("sData = " + sData);
    	return Integer.valueOf(sData);
    }
}

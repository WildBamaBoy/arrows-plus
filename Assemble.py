#!/usr/bin/env python

#-------------------------------------------------------------------------------
# Assemble.py
# Prepares Arrows Plus to be distributed by packaging the mod
# archive and source archives.
#
# This file, both in source code form or as a compiled binary, is free and
# released into the public domain.
#-------------------------------------------------------------------------------

import os
import sys
import shutil
import zipfile
import subprocess
import traceback
import time

releaseVersion = ""
baseFolder     = os.getcwd() + "/"
projectFolder  = baseFolder + "Forge/mcp/"
buildFolder    = baseFolder + "Build/"
reobfFolder    = projectFolder + "/reobf/minecraft/arrowsplus/"
biomesFolder   = projectFolder + "/reobf/minecraft/biomesoplenty/"
sourceFolder   = projectFolder + "/modsrc/minecraft/arrowsplus/"
assetsFolder   = projectFolder + "/src/minecraft/assets/"
    
def main():
    print "---------------------------------------------"
    print "Arrows Plus Packaging Script"
    print "---------------------------------------------"

    #Get release version
    releaseVersion = raw_input("Enter version: ")

    #Check whether to run MCP again or not.
    choice = raw_input("Run MCP? [Y/N]: ")
    
    if (choice == "Y" or choice == "y"):
        os.chdir(projectFolder)
        insertBlank()
        recompile()
        reobfuscate()

    #Clear the build folder
    if (os.path.exists(buildFolder)):
        print "Removing build folder..."
        shutil.rmtree(buildFolder)
        print "Waiting to continue..."
        time.sleep(1)

    os.mkdir(buildFolder)
    
    insertBlank()
    print "---------------------------------------"
    print "Packaging release archive..."
    print "---------------------------------------"
    insertBlank()
    
    #Zip up the mod archive.
    modArchive = zipfile.ZipFile(buildFolder + "/Arrows Plus " + releaseVersion + ".zip", "w", zipfile.ZIP_DEFLATED)
    modFiles = os.listdir(buildFolder)

    modArchive.write(projectFolder + "src/minecraft/arrowsplus.png", "arrowsplus.png")
    modArchive.write(projectFolder + "src/minecraft/mcmod.info", "mcmod.info")
    modArchive.write(baseFolder + "LICENSE", "_LICENSE.txt")

    print "Zipping assets..."
    for root, dirs, files in os.walk(assetsFolder):
        for fileName in files:
            fullPath = os.path.join(root, fileName)
            modArchive.write(fullPath, fullPath.replace(assetsFolder, "assets/"))

    print "Zipping compiled classes..."
    for root, dirs, files in os.walk(reobfFolder):
        for fileName in files:
            fullPath = os.path.join(root, fileName)
            modArchive.write(fullPath, fullPath.replace(reobfFolder, "arrowsplus/"))

    for root, dirs, files in os.walk(biomesFolder):
        for fileName in files:
            fullPath = os.path.join(root, fileName)
            modArchive.write(fullPath, fullPath.replace(biomesFolder, "biomesoplenty/"))
            
    modArchive.close()

    #Zip up the source.
    linesOfCode = 0
    
    print "Zipping up source archive..."
    getSource()
    sourceArchive = zipfile.ZipFile(buildFolder + "/Arrows Plus " + releaseVersion + " - Source.zip", "w", zipfile.ZIP_DEFLATED)
    sourceFiles = os.listdir(sourceFolder)

    for root, dirs, files in os.walk(sourceFolder):
        for fileName in files:
            containsCorrectHeader = True
            fullPath = os.path.join(root, fileName)
            archiveName = fullPath.replace(sourceFolder, "arrowsplus/")
            sourceArchive.write(fullPath, archiveName)

            with open(fullPath) as f:
                lines = f.readlines()

                for line in lines:
                    linesOfCode += 1

                    if fileName in lines:
                        containsCorrectHeader = True

            if not containsCorrectHeader:
                print "WARNING: Malformed header on " + fileName + "."

    print str(linesOfCode) + " lines."
    insertBlank()
    sourceArchive.close()

    print "--------------------------"
    print "Packaging complete."
    print "--------------------------"
    os.system("pause")

def recompile():
    os.chdir(projectFolder)
    subprocess.call("recompile.bat")
    insertBlank()

def reobfuscate():
    os.chdir(projectFolder)
    subprocess.call("reobfuscate.bat")
    insertBlank()

def getSource():
    os.chdir(projectFolder)
    subprocess.call("getchangedsrc.bat")
    insertBlank()
            
def insertBlank():
    print ""
    
if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print "!!!!!!!!!!!!Unexpected exception!!!!!!!!!!!!"
        print e
        traceback.print_exc()
        os.system("pause")

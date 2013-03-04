var imgNr = imgNr;
macro "Max proj" {
close("\\Others")
filepath = getArgument();
imgNr = imgNr + 1;
print(imgNr);
//filepath = "C:\\FolderName\\W001_P001_T013.lsm";
MaxImg = 2;
if (endsWith(filepath,"lsm")) {
run("Bio-Formats Importer", "open=" + filepath + " autoscale color_mode=Default view=Hyperstack stack_order=XYCZT");
title = getTitle();
//call("ij.gui.ImageWindow.setNextLocation", 0, 0)
run("Z Project...", "start=1 stop=" + getSliceNumber() + " projection=[Max Intensity]");

close(title);
//titles = Array.concat(getTitle(), titles);
//Array.print(titles);
//for ( i = MaxImg; i < titles.length-1; i++ ) {
//	close(titles[i]);
//}
//titles = Array.trim(titles, MaxImg+1);
}
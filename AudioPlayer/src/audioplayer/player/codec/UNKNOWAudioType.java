package audioplayer.player.codec;

public class UNKNOWAudioType extends AudioType{
	{
		name = "Unknow";
		description = "Unknown file";
		extentions = new String[]{".*"};
		pplClass = AudioProcessingLayer.class;
	}
}

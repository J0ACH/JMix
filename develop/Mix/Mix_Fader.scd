SynthDef(\Mix_Fader, { | in, out, amp, mute |
	var numCh, tone;
	numCh = in.numChannels;
	tone = In.ar(in, numCh);
	Out.ar(out, Splay.ar(tone * amp * mute),0)
});
SynthDef(\Mix_Flatten2Chnl, { | bus |
	var numCh, tone, bus2;
	numCh = bus.numChannels;
	tone = Splay.ar(In.ar(bus, numCh),0);
	bus2 = Bus.audio(s,2);
	Out.ar(bus2, tone);
});
// out of order

SynthDef(\Efx_Pan, { |bus, freq, mult|
	var tone, efx, chR, chL;
	tone = In.ar(bus, 2);
	efx = Mix.new(tone);
	// chR = tone[0];
	// chL = tone[1];
	// efx = chR + chL;
	ReplaceOut.ar(bus,Pan2.ar(efx, SinOsc.kr(freq, 0, mult),1));
	// ReplaceOut.ar(bus, In.ar(bus, 2) * SinOsc.kr(freq, [0,0.5pi], mult));
	},
	metadata: (
		specs: (
			freq: ControlSpec(0.1, 8.0, \lin, 0.1, 1),
			mult: ControlSpec(0.1, 1.0, \lin, 0.05, 1)
		)
	)
)

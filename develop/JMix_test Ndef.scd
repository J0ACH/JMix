m = JMix(4);
m.gui;
// s.plotTree;
(
Ndef(\aaa).group = m.inGroup;
Ndef(\aaa, {
	var osc;
	osc = SinOsc.ar(220!8, 0, SinOsc.kr(0.5,0,0.3,0.4));

	Out.ar(m.ch(0),osc);
}).play;
)
Ndef(\aaa).free;
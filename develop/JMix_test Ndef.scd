m = JMix(4);
m.gui;

(
Ndef(\aaa).group = m.synG;
Ndef(\aaa, {
	var osc;
	osc = SinOsc.ar(220!2, 0, SinOsc.kr(0.5,0,0.3,0.4));
	Out.ar(m.ch(0),osc);

}).play;
)
s.plotTree;
Ndef(\aaa).clear;
Ndef(\aaa).free;
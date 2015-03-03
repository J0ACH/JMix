JMix {
	classvar version = 0.16;
	classvar server;

	classvar mixSDef, efxSDef;
	var numCh, <numEfx;
	var jmixG, <masterG, synG;
	var synG_nodeID;
	var coll_Channels;

	var masterSynth, master_aBus;

	var <win, mixFrame;

	var <sizeXChnl, <sizeYChnl;
	var <colBack, <colFront, <colActive, <colChange;
	var <fontBig, <fontSmall;

	*new { |numChannels|
		^super.new.init(numChannels);
	}

	init { |xCh|
		server = Server.default;
		mixSDef = this.storeSynth(this.folderMix); // Mix_Fader = 0; Mix_Limiter = 1; Mix_NewVal = 2
		efxSDef = this.storeSynth(this.folderEfx);
		numCh = xCh;
		numEfx = efxSDef.size;

		server.waitForBoot{
			// here is problem with Ndef, ndef.free kill synG and playing doesnt continue

			// g = Group.basicNew(s, 3000); // Create without sending
			// s.sendBundle(nil, g.newMsg;)

			jmixG = Group.new(addAction:\addToTail);
			masterG = Group.new(jmixG, \addToTail);

			// synG = this.inGroup; // add if Ndef kill this group
			synG = Group.new(jmixG, \addToHead);
			synG_nodeID = synG.nodeID;

			master_aBus = Bus.audio(server, 2);
			masterSynth = Synth(this.mixSynthDef(1),[\bus, master_aBus],masterG);

			coll_Channels = List.new(numCh);
			numCh.do { |i|
				coll_Channels.add(this.addChannel(i));
			};
		}
	}

	inGroup{
		// this works but with gap
		// synG.free;
		// synG = Group.new(jmixG, \addToHead);
		/*
		if(synG.notNil){
		// ("synG.isRunning :" ++ synG.isRunning).postln;
		("synG run").postln;
		}
		{
		("synG nil").postln;
		}
		*/
		var synG_ID;
		(
			try {
				// synG_nodeID = synG.nodeID;
				("synG nodeID :" ++ synG_nodeID).postln;

				// } {|error| \caught.postln; error.dump }
			}{|error|
				("synG nodeID nil ").postln;
				synG = Group.new(jmixG, \addToHead);
				error.dump;
			}
		);


		// synG = Group.basicNew(server, 999); // Create without sending
		// server.sendBundle(nil, synG.newMsg;);


		// ("new synG nodeID : " ++ synG.nodeID).postln;

		^synG;
	}

	storeSynth {|dir, libname=\global, completionMsg, keepDef = true|
		var dict, list;
		dict = IdentityDictionary.new;
		(dir ++ "\/*.scd").pathMatch.do{ |path|
			dict.put(path.basename.splitext[0].asSymbol, 0);
		};

		dict.keysDo{|key|
			if(dict[key]==0){
				dict[key] = thisProcess.interpreter.compileFile("%\/%.scd".format(dir, key)).value;
			};
		};

		dict.do{|def|
			def.add(libname, completionMsg, keepDef);
		};

		list = dict.keys(Array);
		list.sort;
		^list;
	}

	printMix{^("list of prepared efx synth: " ++ mixSDef);}

	printEfx{

		var answ = " ";
		"\nlist of prepared efx synth: ".postln;

		efxSDef.size.do{|i|

			( i.asString ++ " : " ++ efxSDef[i].asString).postln;
			// ( this.channel(0).efx(i).controlNames.size).postln;

			this.channel(0).efx(i).controlNames.size.do{|j|
				// answ = answ ++ "jsem";
				( "  - " ++ j.asString ++ " : " ++ this.channel(0).efx(i).controlNames[j].asString).postln;
			};
			// "\n".postln;
		};

		// postf("list of prepared efx synth: %\n", "is", "test", pi.round(1e-4), (1..4));
		^""; //
	}




	addChannel{|num|
		var chnl;
		chnl = JMix_channel(this);
		chnl.id = num;
		chnl.buildEfx(efxSDef);
		^chnl;
	}

	addEfx{|numChnl, numEfx|
		this.channel(numChnl).efx(numEfx).add;
		^("efx synth " ++ efxSDef[numEfx] ++ " added to JMix channel " ++ numChnl);
	}
	freeEfx{|numChnl, numEfx|
		this.channel(numChnl).efx(numEfx).free;
		^("efx synth " ++ efxSDef[numEfx] ++ " removed from JMix channel " ++ numChnl);
	}

	gui {
		sizeXChnl = 65;
		sizeYChnl = 500;

		colBack = Color.new255(30,30,30);
		colFront = Color.new255(255,255,255);
		colActive = Color.new255(200,50,50);
		colChange = Color.new255(75,65,45);
		fontBig = Font("Segoe UI", 7,true, isPointSize:true);
		fontSmall = Font("Segoe UI", 6, isPointSize:true);

		win = Window.new("JMix v"++version, Rect(900,sizeYChnl-50,15+((sizeXChnl+5)*numCh),sizeYChnl+10))
		.alpha_(0.95)
		.alwaysOnTop_(true)
		.background_(colBack)
		.front
		.onClose_({
			this.close;
		});

		mixFrame = UserView(win, Rect(5, 5, win.bounds.width-10, win.bounds.height-10))
		.background_(colBack)
		.drawFunc = {
			Pen.strokeColor = colFront;
			Pen.addRect(Rect(0,0, mixFrame.bounds.width, mixFrame.bounds.height));
			Pen.stroke;
		};

		numCh.do { |i|
			this.channel(i).initGui;
			this.channel(i).initGuiEfx;
		};


	}

	refresh {
		"refresh".postln;
		win.refresh;
		mixFrame.refresh;

		numCh.do { |i|
			this.channel(i).refreshGui;
			this.channel(i).refreshGuiEfx;

		};
	}

	mixSynthDef {|num| ^mixSDef[num];}
	efxSynthDef {|num| ^efxSDef[num];}

	channel{ |num| ^coll_Channels[num]; }

	audioBus { ^master_aBus; }
	ch{ |num| ^coll_Channels[num].audioBus; }

	folderRoot{ ^Platform.systemExtensionDir ++ "\/JMix"; }
	folderMix{ ^this.folderRoot ++ "\/Mix"; }
	folderEfx{ ^this.folderRoot ++ "\/Efx"; }

	frame { ^mixFrame; }

	close{
		numCh.do { |i|
			this.channel(i).freeFqv;
		};
		win.close;
		"JMix closed".postln;
	}
	free{
		this.close;
		numCh.do { |i|
			this.channel(i).free;
		};
		masterSynth.free;
		synG.free;
		masterG.free;
		jmixG.free;
	}

}





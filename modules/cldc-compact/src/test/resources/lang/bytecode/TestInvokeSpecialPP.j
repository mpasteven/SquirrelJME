; -*- Mode: Jasmin; indent-tabs-mode: t; tab-width: 4 -*-
; ---------------------------------------------------------------------------
; Multi-Phasic Applications: SquirrelJME
;     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
; ---------------------------------------------------------------------------
; SquirrelJME is under the GNU General Public License v3+, or later.
; See license.mkd for licensing and copyright information.
; ---------------------------------------------------------------------------

.class public lang/bytecode/TestInvokeSpecialPP
.super net/multiphasicapps/tac/TestInteger

.method public <init>()V
	aload 0
	invokenonvirtual net/multiphasicapps/tac/TestInteger/<init>()V
	return
.end method

.method public test()I
.limit stack 2
	aload_0
	invokenonvirtual lang/bytecode/TestInvokeSpecialPP/__method()I

	ireturn
.end method
	
.method __method()I
.limit stack 1
	ldc 1337
	ireturn
.end method

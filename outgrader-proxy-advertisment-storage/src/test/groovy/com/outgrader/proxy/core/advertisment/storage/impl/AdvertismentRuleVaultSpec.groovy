package com.outgrader.proxy.core.advertisment.storage.impl

import spock.lang.Specification

import com.outgrader.proxy.core.model.IAdvertismentRule
import com.outgrader.proxy.core.storage.IAdvertismentRuleVault

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.11-SNAPSHOT
 *
 */
class AdvertismentRuleVaultSpec extends Specification {

	IAdvertismentRuleVault vault = new AdvertismentRuleVault()

	IAdvertismentRule rule = Mock(IAdvertismentRule)

	def "check added subvaults available by their keys"() {
		when:
		def subVault = vault.createSubVault('key')

		then:
		subVault != null
		vault.getSubVault('key') == subVault
	}

	def "check rule added to main vault available here and in subvaults"() {
		setup:
		IAdvertismentRuleVault subVault = vault.createSubVault('key')

		when:
		vault.addRule(rule)
		vault.close()

		then:
		vault.getRules().contains(rule)
		subVault.getRules().contains(rule)
	}

	def "check rule added to vault available only in this vault but not in parent"() {
		setup:
		IAdvertismentRuleVault subVault = vault.createSubVault('key')

		when:
		subVault.addRule(rule)
		vault.close()

		then:
		subVault.getRules().contains(rule)
		!vault.getRules().contains(rule)
	}

	def "check rules or parent vault copied to newly added"() {
		setup:
		10.times { vault.addRule(rule) }

		when:
		def subVault = vault.createSubVault('key')
		vault.close()

		then:
		subVault.getRules().size() == 10
		subVault.getRules().contains(rule)
	}

	def "check not rules available on non-closed vault"() {
		when:
		vault.addRule(rule)

		then:
		vault.rules.size() == 0
	}
}

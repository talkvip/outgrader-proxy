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
		setup:
		IAdvertismentRuleVault subVault1 = Mock(IAdvertismentRuleVault)
		IAdvertismentRuleVault subVault2 = Mock(IAdvertismentRuleVault)

		when:
		vault.addSubVault('key1', subVault1)
		vault.addSubVault('key2', subVault2)

		then:
		vault.getSubVault('key1') == subVault1
		vault.getSubVault('key2') == subVault2
	}

	def "check rule added to main vault also added to subvaults"() {
		setup:
		IAdvertismentRuleVault subVault1 = Mock(IAdvertismentRuleVault)
		IAdvertismentRuleVault subVault2 = Mock(IAdvertismentRuleVault)

		vault.addSubVault('key1', subVault1)
		vault.addSubVault('key2', subVault2)

		when:
		vault.addRule(rule)

		then:
		1 * subVault1.addRule(rule)
		1 * subVault2.addRule(rule)
	}

	def "check rule added to main vault available here and in subvaults"() {
		setup:
		IAdvertismentRuleVault subVault1 = new AdvertismentRuleVault()
		IAdvertismentRuleVault subVault2 = new AdvertismentRuleVault()

		vault.addSubVault('key1', subVault1)
		vault.addSubVault('key2', subVault2)

		when:
		vault.addRule(rule)

		then:
		vault.getIncludingRules().contains(rule)
		subVault1.getIncludingRules().contains(rule)
		subVault2.getIncludingRules().contains(rule)
	}

	def "check rule added to vault available only in this vault but not in parent"() {
		setup:
		IAdvertismentRuleVault subVault = new AdvertismentRuleVault()

		vault.addSubVault('key', subVault)

		when:
		subVault.addRule(rule)

		then:
		subVault.getIncludingRules().contains(rule)
		!vault.getIncludingRules().contains(rule)
	}
}

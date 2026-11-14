import json
from pathlib import Path
import unittest

RES = Path(__file__).resolve().parents[2] / 'main' / 'resources'


def read(path):
    return json.loads((RES / path).read_text(encoding='utf-8-sig'))


class VehicleResourcesTest(unittest.TestCase):
    def test_sprut_driver_camera_is_independent_of_turret(self):
        vehicle = read('data/squadmc/sbw/vehicles/sprut.json')
        driver, gunner, commander = vehicle['Seats']
        self.assertEqual(driver['Transform'], 'Vehicle')
        self.assertEqual(driver['CameraPos']['Transform'], 'Vehicle')
        self.assertEqual(driver['CameraPos']['Direction'], 'Vehicle')
        self.assertEqual(driver['CameraPos']['ZoomDirection'], 'Vehicle')
        self.assertEqual(driver['CameraPos']['Position'], [0.05, 2.3, 2.75])
        for actual, expected in zip(driver['Position'], [0, 0.5875, 1.75]):
            self.assertAlmostEqual(actual, expected)
        self.assertEqual(gunner['CameraPos']['Direction'], 'Barrel')
        self.assertEqual(commander['CameraPos']['Direction'], 'WeaponStationBarrel')

    def test_every_vehicle_has_both_names(self):
        for locale in ('ru_ru', 'en_us'):
            language = read(f'assets/squadmc/lang/{locale}.json')
            for path in (RES / 'data/squadmc/sbw/vehicles').glob('*.json'):
                self.assertTrue(language.get('entity.squadmc.' + path.stem), (locale, path.stem))

    def test_mgs_seats_and_resources(self):
        vehicle = read('data/squadmc/sbw/vehicles/stryker_mgs.json')
        self.assertEqual(vehicle['ID'], 'squadmc:stryker_mgs')
        self.assertEqual(len(vehicle['Seats']), 3)
        for seat in vehicle['Seats']:
            for weapon in seat.get('Weapons', []):
                self.assertIn(weapon, vehicle['Weapons'])
        self.assertEqual(vehicle['Weapons']['Cannon']['Projectile'], 'superbwarfare:cannon_shell')
        geometry = read('assets/squadmc/geo/stryker_mgs.geo.json')['minecraft:geometry'][0]
        bones = [bone['name'] for bone in geometry['bones']]
        self.assertEqual(len(bones), len(set(bones)))
        self.assertTrue({'root', 'turret', 'barrel', 'BarrelOccilator'} <= set(bones))
        for bone in geometry['bones']:
            if 'parent' in bone:
                self.assertIn(bone['parent'], bones)
        self.assertTrue((RES / 'assets/squadmc/textures/entity/stryker.png').is_file())

    def test_light_vehicle_allowlist(self):
        allowed = read('data/squadmc/tags/entity_types/tacz_vulnerable_wheels.json')['values']
        for vehicle in ('btr80', 'btr82', 'lav25', 'stryker_m2', 'stryker_mgs', 'stryker_mortar', 'brdm2'):
            self.assertNotIn('squadmc:' + vehicle, allowed)
        for vehicle in ('fmtv', 'ural_green', 'kamaz', 'humvee', 'uaz', 'toyota'):
            self.assertIn('squadmc:' + vehicle, allowed)
        for identifier in allowed:
            self.assertTrue((RES / ('data/squadmc/sbw/vehicles/' + identifier.split(':')[1] + '.json')).is_file())

    def test_protected_terrain(self):
        protected = read('data/squadmc/tags/blocks/explosion_protected.json')['values']
        self.assertTrue({'minecraft:rooted_dirt', 'minecraft:coarse_dirt', 'minecraft:sand'} <= set(protected))

    def test_mortar_damage_and_radius(self):
        mortar = read('data/squadmc/sbw/vehicles/stryker_mortar.json')['Weapons']['Mortar']
        self.assertEqual(mortar['ExplosionRadius'], 36 * 1.5)
        self.assertEqual(mortar['ExplosionDamage'], 300)

    def test_unarmed_pilot_has_hud_data(self):
        vehicle = read('data/squadmc/sbw/vehicles/littlebird.json')
        self.assertEqual(vehicle['HudType'], '@Helicopter')
        instrument = vehicle['Weapons'][vehicle['Seats'][0]['Weapons'][0]]
        self.assertEqual(instrument['RPM'], 0)
        self.assertEqual(instrument['Damage'], 0)
        self.assertNotIn('Projectile', instrument)


if __name__ == '__main__':
    unittest.main()

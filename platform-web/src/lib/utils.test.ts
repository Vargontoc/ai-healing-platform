import { describe, it, expect } from 'vitest'
import { cn } from './utils'

describe('cn utility', () => {
  it('should merge class names correctly', () => {
    expect(cn('px-2 py-1', 'px-4')).toBe('py-1 px-4')
  })

  it('should handle undefined and null values', () => {
    expect(cn('px-2', undefined, 'py-1', null)).toBe('px-2 py-1')
  })

  it('should handle conditional classes', () => {
    expect(cn('px-2', false && 'py-1', 'text-red-500')).toBe('px-2 text-red-500')
  })
})

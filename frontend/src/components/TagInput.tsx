import { useState } from 'react'
import type { KeyboardEvent } from 'react'
import { X } from 'lucide-react'

interface TagInputProps {
  tags: string[]
  onChange: (tags: string[]) => void
  placeholder?: string
}

export function TagInput({ tags, onChange, placeholder }: Readonly<TagInputProps>) {
  const [input, setInput] = useState('')

  function handleKeyDown(e: KeyboardEvent<HTMLInputElement>) {
    if (e.key === 'Enter' && input.trim()) {
      e.preventDefault()
      if (!tags.includes(input.trim())) {
        onChange([...tags, input.trim()])
      }
      setInput('')
    }
    if (e.key === 'Backspace' && !input && tags.length > 0) {
      onChange(tags.slice(0, -1))
    }
  }

  function removeTag(index: number) {
    onChange(tags.filter((_, i) => i !== index))
  }

  return (
    <div className="flex flex-wrap gap-1.5 p-2 bg-bg-card border border-border-card rounded-lg min-h-10.5">
      {tags.map((tag, i) => (
        <span
          key={tag}
          className="flex items-center gap-1 px-2 py-0.5 bg-accent/15 text-accent text-xs rounded"
        >
          {tag}
          <button type="button" onClick={() => removeTag(i)} className="hover:text-white">
            <X className="w-3 h-3" />
          </button>
        </span>
      ))}
      <input
        value={input}
        onChange={(e) => setInput(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder={tags.length === 0 ? placeholder : ''}
        className="flex-1 min-w-25 bg-transparent outline-none text-sm text-text-primary placeholder:text-text-secondary"
      />
    </div>
  )
}
